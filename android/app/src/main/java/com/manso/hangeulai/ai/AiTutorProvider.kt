package com.manso.hangeulai.ai

import android.content.Context
import android.net.Uri
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.SamplerConfig
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AiTutorProvider : AutoCloseable {
    val isReady: Boolean
    suspend fun initialize(): Result<Unit>
    suspend fun ask(request: TutorRequest): Result<String>
}

data class TutorRequest(
    val sentence: String,
    val translation: String,
    val level: String,
    val learnerLanguage: String = "English",
    val question: String
)

class MockAiTutorProvider : AiTutorProvider {
    override val isReady: Boolean = true
    override suspend fun initialize(): Result<Unit> = Result.success(Unit)

    override suspend fun ask(request: TutorRequest): Result<String> = Result.success(
        when {
            request.question.contains("문법") || request.question.contains("이다") ->
                "‘이다’는 명사 뒤에 붙어 ‘~이다 / to be’라는 뜻을 만드는 표현이에요. ‘반이다’는 ‘it is half’라는 의미입니다."
            request.question.contains("예문") ->
                "예문: 새로운 시작이다. / 이제 시작이다. / 한국어 공부를 시작해요."
            request.question.contains("퀴즈") ->
                "퀴즈: ‘반’의 뜻은 무엇일까요? ① start ② half ③ person ④ day"
            else ->
                "초급 학습자에게 쉽게 설명하면, ‘${request.sentence}’는 무언가를 시작하는 것 자체가 매우 중요하다는 뜻이에요."
        }
    )

    override fun close() = Unit
}

class LiteRtGemmaTutorProvider(
    private val context: Context,
    private val modelPath: String,
) : AiTutorProvider {
    private var engine: Engine? = null
    private var conversation: Conversation? = null

    override val isReady: Boolean
        get() = engine?.isInitialized() == true && conversation != null

    override suspend fun initialize(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            close()
            val model = File(modelPath)
            require(model.exists() && model.isFile) { "Gemma 모델 파일을 찾을 수 없습니다." }

            val newEngine = Engine(
                EngineConfig(
                    modelPath = model.absolutePath,
                    backend = Backend.CPU(threadCount = 4),
                    maxNumTokens = 1280,
                    cacheDir = context.cacheDir.absolutePath,
                )
            )
            newEngine.initialize()

            val systemInstruction = Contents.of(
                """
                You are Hangeul AI, a Korean language tutor for beginner learners.
                The learner may ask in Korean or English.
                Explain Korean simply and accurately at A1-A2 level.
                Prefer the learner's language for explanations, while preserving Korean examples.
                When useful, structure answers as: Meaning, Vocabulary, Grammar, Examples, Practice.
                Keep answers concise. Do not invent cultural or grammar facts when uncertain.
                """.trimIndent()
            )

            val newConversation = newEngine.createConversation(
                ConversationConfig(
                    systemInstruction = systemInstruction,
                    samplerConfig = SamplerConfig(topK = 32, topP = 0.9, temperature = 0.5),
                )
            )

            engine = newEngine
            conversation = newConversation
        }
    }

    override suspend fun ask(request: TutorRequest): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val activeConversation = checkNotNull(conversation) { "Gemma 모델이 아직 준비되지 않았습니다." }
            val prompt = """
                Current Korean lesson:
                - Sentence: ${request.sentence}
                - Translation: ${request.translation}
                - Learner level: ${request.level}
                - Learner language: ${request.learnerLanguage}

                Learner question:
                ${request.question}

                Answer as a supportive Korean tutor. Keep the explanation concise and suitable for the learner level.
            """.trimIndent()

            activeConversation.sendMessage(prompt).toString().trim()
        }
    }

    override fun close() {
        runCatching { conversation?.close() }
        conversation = null
        runCatching { engine?.close() }
        engine = null
    }

    companion object {
        suspend fun importModel(context: Context, uri: Uri): Result<File> = withContext(Dispatchers.IO) {
            runCatching {
                val targetDir = File(context.filesDir, "models").apply { mkdirs() }
                val target = File(targetDir, "gemma3-1b-it.litertlm")
                context.contentResolver.openInputStream(uri).use { input ->
                    requireNotNull(input) { "선택한 모델 파일을 열 수 없습니다." }
                    target.outputStream().use { output -> input.copyTo(output, bufferSize = 1024 * 1024) }
                }
                require(target.length() > 10L * 1024L * 1024L) { "선택한 파일이 너무 작습니다. .litertlm 모델인지 확인하세요." }
                target
            }
        }

        fun installedModel(context: Context): File = File(context.filesDir, "models/gemma3-1b-it.litertlm")
    }
}

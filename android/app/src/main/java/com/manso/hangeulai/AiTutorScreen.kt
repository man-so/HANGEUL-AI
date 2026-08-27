package com.manso.hangeulai

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manso.hangeulai.ai.LiteRtGemmaTutorProvider
import com.manso.hangeulai.ai.MockAiTutorProvider
import com.manso.hangeulai.ai.TutorRequest
import kotlinx.coroutines.launch

private val TutorInk = Color(0xFF121212)
private val TutorPurple = Color(0xFF7457FF)
private val TutorMuted = Color(0xFFAAA8B0)
private val TutorPanel = Color(0xFF242035)

@Composable
fun AiTutorScreen(lesson: Lesson) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mockProvider = remember { MockAiTutorProvider() }

    var gemmaProvider by remember { mutableStateOf<LiteRtGemmaTutorProvider?>(null) }
    var modelStatus by remember { mutableStateOf("Gemma 모델 없음 · Mock AI 사용 중") }
    var isGemmaReady by remember { mutableStateOf(false) }
    var isBusy by remember { mutableStateOf(false) }
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("궁금한 표현을 선택하거나 직접 질문해보세요.") }

    fun requestFor(prompt: String) = TutorRequest(
        sentence = lesson.korean,
        translation = lesson.translation,
        level = lesson.level,
        learnerLanguage = "Korean",
        question = prompt
    )

    fun submit(prompt: String) {
        if (prompt.isBlank() || isBusy) return
        scope.launch {
            isBusy = true
            val request = requestFor(prompt)
            val gemmaResult = if (isGemmaReady) gemmaProvider?.ask(request) else null
            if (gemmaResult?.isSuccess == true) {
                answer = gemmaResult.getOrThrow()
                modelStatus = "On-device Gemma · 준비됨"
            } else {
                if (isGemmaReady && gemmaResult?.isFailure == true) {
                    modelStatus = "Gemma 응답 오류 · Mock AI로 대체"
                }
                answer = mockProvider.ask(request).getOrElse {
                    "답변을 만들지 못했습니다. 잠시 후 다시 시도해주세요."
                }
            }
            isBusy = false
        }
    }

    fun initializeGemma(provider: LiteRtGemmaTutorProvider) {
        scope.launch {
            isBusy = true
            modelStatus = "Gemma 모델 초기화 중…"
            val result = provider.initialize()
            if (result.isSuccess) {
                gemmaProvider?.takeIf { it !== provider }?.close()
                gemmaProvider = provider
                isGemmaReady = true
                modelStatus = "On-device Gemma · 준비됨"
            } else {
                provider.close()
                isGemmaReady = false
                modelStatus = "Gemma 초기화 실패 · Mock AI 사용 중"
                answer = result.exceptionOrNull()?.message ?: "모델 초기화에 실패했습니다."
            }
            isBusy = false
        }
    }

    val modelPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                isBusy = true
                modelStatus = "Gemma 모델 가져오는 중…"
                val imported = LiteRtGemmaTutorProvider.importModel(context, uri)
                if (imported.isSuccess) {
                    val provider = LiteRtGemmaTutorProvider(context, imported.getOrThrow().absolutePath)
                    isBusy = false
                    initializeGemma(provider)
                } else {
                    isBusy = false
                    isGemmaReady = false
                    modelStatus = "모델 가져오기 실패 · Mock AI 사용 중"
                    answer = imported.exceptionOrNull()?.message ?: "모델 파일을 가져오지 못했습니다."
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val installed = LiteRtGemmaTutorProvider.installedModel(context)
        if (installed.exists()) {
            initializeGemma(LiteRtGemmaTutorProvider(context, installed.absolutePath))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            gemmaProvider?.close()
            mockProvider.close()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("AI 튜터", color = TutorPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("궁금한 건 바로 묻고\n계속 배워요.", fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text("Gemma 모델을 가져오면 질문이 스마트폰 안에서 처리됩니다. 모델이 없거나 오류가 나면 Mock AI로 안전하게 대체됩니다.", color = Color.Gray, lineHeight = 22.sp)
        Spacer(Modifier.height(18.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("온디바이스 AI", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Spacer(Modifier.height(6.dp))
                Text(modelStatus, color = if (isGemmaReady) TutorPurple else Color.Gray, fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                OutlinedButton(
                    onClick = { modelPicker.launch(arrayOf("*/*")) },
                    enabled = !isBusy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isGemmaReady) "Gemma 모델 다시 선택" else "Gemma 모델 가져오기")
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = TutorInk),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(22.dp)) {
                Text("✦ AI 한국어 튜터", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("현재 학습 · ${lesson.korean}", color = TutorMuted, fontSize = 12.sp)
                Spacer(Modifier.height(22.dp))

                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(TutorPanel, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Text(if (isBusy) "생각하는 중…" else answer, color = Color.White, lineHeight = 23.sp)
                }
                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = { submit("쉽게 설명해줘") }, enabled = !isBusy) { Text("쉽게 설명", color = Color.White) }
                    TextButton(onClick = { submit("예문을 보여줘") }, enabled = !isBusy) { Text("예문", color = Color.White) }
                    TextButton(onClick = { submit("문법을 설명해줘") }, enabled = !isBusy) { Text("문법", color = Color.White) }
                    TextButton(onClick = { submit("이 문장으로 퀴즈를 만들어줘") }, enabled = !isBusy) { Text("퀴즈", color = Color.White) }
                }

                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("AI 튜터에게 질문") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isBusy,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color(0xFFB7B4BE),
                        cursorColor = Color.White,
                        focusedBorderColor = TutorPurple,
                        unfocusedBorderColor = Color(0xFF57535F),
                        focusedLabelColor = Color(0xFFCFC5FF),
                        unfocusedLabelColor = Color(0xFFB7B4BE)
                    )
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        val prompt = question.trim()
                        if (prompt.isNotEmpty()) {
                            question = ""
                            submit(prompt)
                        }
                    },
                    enabled = question.isNotBlank() && !isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = TutorPurple),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isBusy) "처리 중…" else "질문하기")
                }
            }
        }
    }
}

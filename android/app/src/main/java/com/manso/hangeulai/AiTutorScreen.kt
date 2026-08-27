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
private val TutorGreen = Color(0xFF2E7D4F)

@Composable
fun AiTutorScreen(lesson: Lesson) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mockProvider = remember { MockAiTutorProvider() }

    var gemmaProvider by remember { mutableStateOf<LiteRtGemmaTutorProvider?>(null) }
    var modelStatus by remember { mutableStateOf("AI 모델이 아직 설치되지 않았어요") }
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
                modelStatus = "Gemma 3 1B · 준비됨 ✓"
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
            modelStatus = "Gemma 3 1B 초기화 중…"
            val result = provider.initialize()
            if (result.isSuccess) {
                gemmaProvider?.takeIf { it !== provider }?.close()
                gemmaProvider = provider
                isGemmaReady = true
                modelStatus = "Gemma 3 1B · 준비됨 ✓"
            } else {
                provider.close()
                isGemmaReady = false
                modelStatus = "Gemma 초기화 실패 · Mock AI 사용 중"
                answer = result.exceptionOrNull()?.message ?: "모델 초기화에 실패했습니다."
            }
            isBusy = false
        }
    }

    val modelPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                isBusy = true
                modelStatus = "모델 가져오는 중… 약 600MB"
                val imported = LiteRtGemmaTutorProvider.importModel(context, uri)
                if (imported.isSuccess) {
                    modelStatus = "모델 복사 완료 · 초기화 중…"
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
        if (installed.exists()) initializeGemma(LiteRtGemmaTutorProvider(context, installed.absolutePath))
    }

    DisposableEffect(Unit) {
        onDispose {
            gemmaProvider?.close()
            mockProvider.close()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Text("AI 튜터", color = TutorPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("궁금한 건 바로 묻고\n계속 배워요.", fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text("Gemma를 설치하면 질문과 답변을 스마트폰 안에서 처리할 수 있어요.", color = Color.Gray, lineHeight = 22.sp)
        Spacer(Modifier.height(18.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("온디바이스 AI", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Spacer(Modifier.height(5.dp))
                Text(modelStatus, color = if (isGemmaReady) TutorGreen else Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(14.dp))

                if (!isGemmaReady) {
                    Text("권장 모델", color = Color.Gray, fontSize = 11.sp)
                    Text("Gemma 3 1B IT", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                    Text("약 584MB · 최초 1회 설치 · Wi-Fi 권장", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { modelPicker.launch(arrayOf("application/octet-stream", "*/*")) },
                        enabled = !isBusy,
                        colors = ButtonDefaults.buttonColors(containerColor = TutorPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (isBusy) "처리 중…" else "Gemma 모델 가져오기") }
                    Spacer(Modifier.height(10.dp))
                    Text("현재 테스트 버전에서는 .litertlm 파일을 한 번 선택해야 합니다. AI Edge Gallery가 받은 모델은 Android 앱 전용 저장공간에 있어 Hangeul AI가 직접 읽을 수 없습니다.", color = Color.Gray, fontSize = 11.sp, lineHeight = 17.sp)
                } else {
                    Text("이제 파일을 다시 선택할 필요 없이 AI 튜터를 사용할 수 있어요.", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { modelPicker.launch(arrayOf("application/octet-stream", "*/*")) },
                        enabled = !isBusy,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("다른 모델로 교체") }
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
                Box(Modifier.fillMaxWidth().background(TutorPanel, RoundedCornerShape(18.dp)).padding(16.dp)) {
                    Text(if (isBusy) "생각하는 중…" else answer, color = Color.White, lineHeight = 23.sp)
                }
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        unfocusedBorderColor = Color(0xFF77727E),
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
                ) { Text(if (isBusy) "처리 중…" else "질문하기") }
            }
        }
    }
}

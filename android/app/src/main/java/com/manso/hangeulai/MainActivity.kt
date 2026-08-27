package com.manso.hangeulai

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val WarmBackground = Color(0xFFF7F5F0)
private val Ink = Color(0xFF121212)
private val Accent = Color(0xFFE84C3D)
private val Purple = Color(0xFF7457FF)
private val SoftPurple = Color(0xFFE9DDFC)
private val Muted = Color(0xFF6E6B65)

private enum class AppTab(val label: String, val icon: String) {
    Home("홈", "⌂"),
    Learn("학습", "📖"),
    Write("쓰기", "✍"),
    Tutor("AI 튜터", "✦")
}

data class Lesson(
    val korean: String,
    val translation: String,
    val level: String,
    val explanation: String,
    val vocabulary: List<Pair<String, String>>
)

private val sampleLesson = Lesson(
    korean = "시작이 반이다.",
    translation = "Well begun is half done.",
    level = "A1",
    explanation = "무언가를 시작하는 일이 가장 어려울 때가 많아요. 일단 시작했다면 이미 중요한 첫걸음을 내디딘 것입니다.",
    vocabulary = listOf(
        "시작" to "start · beginning",
        "반" to "half",
        "이다" to "to be"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HangeulAiApp()
            }
        }
    }
}

@Composable
private fun HangeulAiApp() {
    var selectedTab by remember { mutableStateOf(AppTab.Home) }

    Scaffold(
        containerColor = WarmBackground,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Text(tab.icon, fontSize = 18.sp) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.Home -> HomeScreen(
                    lesson = sampleLesson,
                    onLearn = { selectedTab = AppTab.Learn },
                    onWrite = { selectedTab = AppTab.Write },
                    onTutor = { selectedTab = AppTab.Tutor }
                )
                AppTab.Learn -> LearnScreen(sampleLesson)
                AppTab.Write -> WritingScreen()
                AppTab.Tutor -> TutorScreen(sampleLesson)
            }
        }
    }
}

@Composable
private fun HomeScreen(
    lesson: Lesson,
    onLearn: () -> Unit,
    onWrite: () -> Unit,
    onTutor: () -> Unit
) {
    val context = LocalContext.current
    val saved = remember {
        context.getSharedPreferences("hangeul_ai", 0)
            .getBoolean("saved_${lesson.korean}", false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text("HANGEUL AI", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Text("오늘도 한국어\n한 걸음!", fontSize = 36.sp, lineHeight = 40.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        Text("읽고, 듣고, 쓰고, 궁금한 건 AI 튜터에게 물어보세요.", color = Muted, lineHeight = 22.sp)
        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("오늘의 한국어", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.weight(1f))
                    Text(lesson.level, modifier = Modifier.background(WarmBackground, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 5.dp), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(18.dp))
                Text(lesson.korean, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(7.dp))
                Text(lesson.translation, color = Muted, fontSize = 16.sp)
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = onLearn,
                    colors = ButtonDefaults.buttonColors(containerColor = Ink),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("오늘 학습하기 →", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(26.dp))
        Text("오늘의 학습", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        Spacer(Modifier.height(12.dp))
        HomeActionCard("📖", "한 문장 배우기", "어휘 · 문법 · 듣기", onLearn)
        Spacer(Modifier.height(10.dp))
        HomeActionCard("✍", "한글 쓰기", "오늘의 글자 · ㄱ", onWrite)
        Spacer(Modifier.height(10.dp))
        HomeActionCard("✦", "AI 한국어 튜터", "궁금한 표현 물어보기", onTutor, accent = Purple)

        Spacer(Modifier.height(26.dp))
        Text("나의 학습", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            MiniStatCard("저장한 문장", if (saved) "1" else "0", Modifier.weight(1f))
            MiniStatCard("오늘 목표", "0 / 3", Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        Text("학습 기록과 연속 학습 일수는 다음 버전에서 실제 데이터와 연결할 예정입니다.", color = Color.Gray, fontSize = 12.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun HomeActionCard(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    accent: Color = Ink
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(46.dp).background(
                    if (accent == Purple) SoftPurple else WarmBackground,
                    RoundedCornerShape(15.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, color = accent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = Muted, fontSize = 13.sp)
            }
            Text("→", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun MiniStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(label, color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        }
    }
}

@Composable
private fun LearnScreen(lesson: Lesson) {
    val context = LocalContext.current
    var rate by remember { mutableStateOf(0.9f) }
    var saved by remember {
        mutableStateOf(
            context.getSharedPreferences("hangeul_ai", 0)
                .getBoolean("saved_${lesson.korean}", false)
        )
    }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.KOREAN
            }
        }
        tts = engine
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text("오늘의 한국어", color = Accent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        Text("한 문장으로 배우는\n진짜 한국어", fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TODAY'S KOREAN", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text(lesson.level, modifier = Modifier.background(WarmBackground, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 5.dp), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(20.dp))
                Text(lesson.korean, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Text(lesson.translation, fontSize = 17.sp, color = Muted)
                Spacer(Modifier.height(22.dp))

                lesson.vocabulary.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        row.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(WarmBackground, RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(item.first, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(item.second, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = rate == 0.7f, onClick = { rate = 0.7f }, label = { Text("0.7× 천천히") })
                    FilterChip(selected = rate == 0.9f, onClick = { rate = 0.9f }, label = { Text("1.0× 일반") })
                }
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        tts?.setSpeechRate(rate)
                        tts?.speak(lesson.korean, TextToSpeech.QUEUE_FLUSH, null, "lesson")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Ink),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Text("🔊 한국어 듣기", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        Text("문장 이해하기", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(10.dp))
        Text(lesson.explanation, lineHeight = 25.sp, color = Color(0xFF5F5B55))
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = {
                saved = !saved
                context.getSharedPreferences("hangeul_ai", 0).edit()
                    .putBoolean("saved_${lesson.korean}", saved)
                    .apply()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (saved) "★ 저장됨" else "☆ 이 문장 저장")
        }
    }
}

@Composable
private fun WritingScreen() {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("쓰기", color = Accent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("손으로 직접 쓰며\n한글을 배워요.", fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text("먼저 ㄱ을 손가락이나 스타일러스로 따라 써보세요.", color = Color.Gray)
        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("① →   ↓ ②", color = Accent, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(330.dp)
                        .border(2.dp, Color(0xFFE0DDD6), RoundedCornerShape(20.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset -> currentStroke = listOf(offset) },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentStroke = currentStroke + change.position
                                },
                                onDragEnd = {
                                    if (currentStroke.size > 1) strokes.add(currentStroke)
                                    currentStroke = emptyList()
                                }
                            )
                        }
                ) {
                    Text(
                        "ㄱ",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 210.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0x15121212)
                    )
                    Canvas(Modifier.fillMaxSize()) {
                        (strokes + listOf(currentStroke)).forEach { points ->
                            if (points.size > 1) {
                                val path = Path().apply {
                                    moveTo(points.first().x, points.first().y)
                                    points.drop(1).forEach { lineTo(it.x, it.y) }
                                }
                                drawPath(path, color = Ink, style = Stroke(width = 14f, cap = StrokeCap.Round))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { strokes.clear(); currentStroke = emptyList() }) { Text("지우기") }
                    Button(onClick = { strokes.clear(); currentStroke = emptyList() }, colors = ButtonDefaults.buttonColors(containerColor = Ink)) { Text("다음 글자 →") }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("학습 순서", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        ProgressRow("01", "글자", "ㄱ ㄴ ㄷ")
        ProgressRow("02", "음절", "가 나 다")
        ProgressRow("03", "단어", "한국 · 사람")
        ProgressRow("04", "문장", "시작이 반이다.")
    }
}

@Composable
private fun ProgressRow(number: String, title: String, example: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(number, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.width(40.dp))
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
        Text(example, fontSize = 18.sp)
    }
}

@Composable
private fun TutorScreen(lesson: Lesson) {
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("궁금한 표현을 선택하거나 직접 질문해보세요.") }

    fun mockAnswer(prompt: String): String = when {
        prompt.contains("이다") || prompt.contains("문법") -> "‘이다’는 명사 뒤에 붙어 ‘~이다 / to be’라는 뜻을 만드는 표현이에요. ‘반이다’는 ‘it is half’라는 의미입니다."
        prompt.contains("예문") -> "예문: 수업이 시작이다(X) → 수업을 시작해요(O) / 새로운 시작이다. / 이제 시작이다."
        prompt.contains("퀴즈") -> "퀴즈: ‘반’의 뜻은 무엇일까요? ① start ② half ③ person ④ day"
        else -> "초급 학습자에게 쉽게 설명하면, ‘시작이 반이다’는 무언가를 시작하는 것 자체가 성공의 절반만큼 중요하다는 뜻이에요."
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Text("AI 튜터", color = Purple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("궁금한 건 바로 묻고\n계속 배워요.", fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text("현재 단계는 UI와 학습 흐름 검증을 위한 Mock AI입니다. 다음 단계에서 온디바이스 AI Provider로 교체합니다.", color = Color.Gray, lineHeight = 22.sp)
        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Ink),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(22.dp)) {
                Text("✦ AI 한국어 튜터", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("현재 학습 · ${lesson.korean}", color = Color(0xFFAAA8B0), fontSize = 12.sp)
                Spacer(Modifier.height(22.dp))

                Box(Modifier.fillMaxWidth().background(Color(0xFF242035), RoundedCornerShape(18.dp)).padding(16.dp)) {
                    Text(answer, color = Color.White, lineHeight = 23.sp)
                }
                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = { answer = mockAnswer("쉽게 설명") }) { Text("쉽게 설명", color = Color.White) }
                    TextButton(onClick = { answer = mockAnswer("예문") }) { Text("예문", color = Color.White) }
                    TextButton(onClick = { answer = mockAnswer("문법") }) { Text("문법", color = Color.White) }
                    TextButton(onClick = { answer = mockAnswer("퀴즈") }) { Text("퀴즈", color = Color.White) }
                }

                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("AI 튜터에게 질문") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (question.isNotBlank()) {
                            answer = mockAnswer(question)
                            question = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("질문하기") }
            }
        }
    }
}

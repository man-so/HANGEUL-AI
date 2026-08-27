from pathlib import Path
import re

p = Path('android/app/src/main/java/com/manso/hangeulai/MainActivity.kt')
s = p.read_text()

s = re.sub(
    r'data class Lesson\([\s\S]*?\n\)\n\nprivate val sampleLesson = Lesson\([\s\S]*?\n\)\n\nclass MainActivity',
    '''data class Lesson(
    val id: String,
    val collection: String,
    val speaker: String,
    val korean: String,
    val simpleKorean: String,
    val translation: String,
    val japanese: String,
    val grammar: List<GrammarPoint>,
    val example: String,
    val level: String,
    val category: List<String>,
    val sourceType: String,
    val explanation: String,
    val vocabulary: List<Pair<String, String>>
)

class MainActivity''',
    s,
    count=1
)

s = s.replace(
    'var selectedTab by remember { mutableStateOf(AppTab.Home) }\n\n    Scaffold(',
    'var selectedTab by remember { mutableStateOf(AppTab.Home) }\n    var lessonIndex by remember { mutableStateOf((java.time.LocalDate.now().dayOfYear - 1) % lessonCatalog.size) }\n    val currentLesson = lessonCatalog[lessonIndex]\n\n    Scaffold('
)
s = s.replace(
    'lesson = sampleLesson,\n                    onLearn = { selectedTab = AppTab.Learn },',
    'lesson = currentLesson,\n                    onNextLesson = { lessonIndex = (lessonIndex + 1) % lessonCatalog.size },\n                    onLearn = { selectedTab = AppTab.Learn },',
    1
)
s = s.replace('AppTab.Learn -> LearnScreen(sampleLesson)', 'AppTab.Learn -> LearnScreen(currentLesson)')
s = s.replace('AppTab.Tutor -> AiTutorScreen(sampleLesson)', 'AppTab.Tutor -> AiTutorScreen(currentLesson)')

s = s.replace(
    'private fun HomeScreen(\n    lesson: Lesson,\n    onLearn: () -> Unit,',
    'private fun HomeScreen(\n    lesson: Lesson,\n    onNextLesson: () -> Unit,\n    onLearn: () -> Unit,'
)

marker = '                Text(lesson.translation, color = Muted, fontSize = 16.sp)\n'
if marker in s and 'lesson.speaker' not in s:
    s = s.replace(
        marker,
        marker + '                Spacer(Modifier.height(7.dp))\n                Text("${lesson.speaker} · ${lesson.level}", color = Color.Gray, fontSize = 12.sp)\n',
        1
    )

btn_end = '                    Text("오늘 학습하기 →", fontWeight = FontWeight.Bold)\n                }'
if btn_end in s and '다른 문장 보기' not in s:
    s = s.replace(
        btn_end,
        btn_end + '\n                Spacer(Modifier.height(8.dp))\n                TextButton(onClick = onNextLesson, modifier = Modifier.align(Alignment.End)) {\n                    Text("다른 문장 보기 ↻", color = Accent, fontWeight = FontWeight.Bold)\n                }',
        1
    )

old = '''        Text("문장 이해하기", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(10.dp))
        Text(lesson.explanation, lineHeight = 25.sp, color = Color(0xFF5F5B55))
        Spacer(Modifier.height(20.dp))'''
new = '''        Text("쉬운 한국어", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text(lesson.simpleKorean, lineHeight = 25.sp, color = Color(0xFF5F5B55))
        Spacer(Modifier.height(18.dp))
        Text("日本語", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(6.dp))
        Text(lesson.japanese, lineHeight = 24.sp, color = Muted)
        Spacer(Modifier.height(22.dp))
        Text("문법 포인트", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(10.dp))
        lesson.grammar.forEach { point ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(point.pattern, color = Accent, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Text(point.meaning, color = Muted, lineHeight = 22.sp, fontSize = 14.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("예문", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text(lesson.example, lineHeight = 24.sp)
        Spacer(Modifier.height(18.dp))
        Text("문장 이해하기", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text(lesson.explanation, lineHeight = 25.sp, color = Color(0xFF5F5B55))
        Spacer(Modifier.height(20.dp))'''
if old in s:
    s = s.replace(old, new, 1)

p.write_text(s)
print('lesson integration applied')

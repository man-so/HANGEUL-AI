from pathlib import Path

p = Path('android/app/src/main/java/com/manso/hangeulai/MainActivity.kt')
s = p.read_text(encoding='utf-8')

marker = 'import java.util.Locale\n'
imports = (
    'import com.manso.hangeulai.expressions.KoreanExpressionRepository\n'
    'import com.manso.hangeulai.expressions.KoreanExpressionScreen\n'
)
if imports not in s:
    if marker not in s:
        raise SystemExit('import marker not found')
    s = s.replace(marker, marker + imports, 1)

old_state = '''    var selectedTab by remember { mutableStateOf(AppTab.Home) }
    var lessonIndex by remember { mutableStateOf((java.time.LocalDate.now().dayOfYear - 1) % lessonCatalog.size) }
    val currentLesson = lessonCatalog[lessonIndex]'''
new_state = '''    var selectedTab by remember { mutableStateOf(AppTab.Home) }
    var lessonIndex by remember { mutableStateOf((java.time.LocalDate.now().dayOfYear - 1) % lessonCatalog.size) }
    val currentLesson = lessonCatalog[lessonIndex]
    val context = LocalContext.current
    val realExpressions = remember { KoreanExpressionRepository.load(context) }
    var showRealExpressions by remember { mutableStateOf(false) }'''
if 'val realExpressions = remember { KoreanExpressionRepository.load(context) }' not in s:
    if old_state not in s:
        raise SystemExit('app state block not found')
    s = s.replace(old_state, new_state, 1)

old_route = 'AppTab.Learn -> LearnScreen(currentLesson)'
new_route = '''AppTab.Learn -> if (showRealExpressions) {
                    KoreanExpressionScreen(
                        expressions = realExpressions,
                        language = "ko",
                        onBack = { showRealExpressions = false }
                    )
                } else {
                    LearnScreen(currentLesson, onOpenRealExpressions = { showRealExpressions = true })
                }'''
if 'KoreanExpressionScreen(' not in s:
    if old_route not in s:
        raise SystemExit('learn route not found')
    s = s.replace(old_route, new_route, 1)

old_sig = 'private fun LearnScreen(lesson: Lesson) {'
new_sig = 'private fun LearnScreen(lesson: Lesson, onOpenRealExpressions: () -> Unit = {}) {'
if new_sig not in s:
    if old_sig not in s:
        raise SystemExit('LearnScreen signature not found')
    s = s.replace(old_sig, new_sig, 1)

button_text = '한국인이 실제로 쓰는 표현 배우기'
if button_text not in s:
    anchor = '''        Spacer(Modifier.height(24.dp))

        Card('''
    insert = '''        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onOpenRealExpressions, modifier = Modifier.fillMaxWidth()) {
            Text("💬 한국인이 실제로 쓰는 표현 배우기")
        }
        Spacer(Modifier.height(16.dp))

        Card('''
    if anchor not in s:
        raise SystemExit('LearnScreen card anchor not found')
    s = s.replace(anchor, insert, 1)

p.write_text(s, encoding='utf-8')
print('MainActivity.kt patched successfully')

from pathlib import Path

p = Path('android/app/src/main/java/com/manso/hangeulai/MainActivity.kt')
s = p.read_text()

if 'import android.speech.tts.Voice' not in s:
    s = s.replace('import android.speech.tts.TextToSpeech\n', 'import android.speech.tts.TextToSpeech\nimport android.speech.tts.Voice\n', 1)

s = s.replace(
    '    var lessonIndex by remember { mutableStateOf((java.time.LocalDate.now().dayOfYear - 1) % lessonCatalog.size) }',
    '    var lessonIndex by remember { mutableStateOf((java.time.LocalDate.now().dayOfYear - 1) % lessonCatalog.size) }\n    var showSettings by remember { mutableStateOf(false) }',
    1,
)

s = s.replace(
    '            when (selectedTab) {\n                AppTab.Home -> HomeScreen(',
    '            if (showSettings) {\n                SettingsScreen(onBack = { showSettings = false })\n            } else when (selectedTab) {\n                AppTab.Home -> HomeScreen(',
    1,
)

s = s.replace(
    '                    onTutor = { selectedTab = AppTab.Tutor }\n                )',
    '                    onTutor = { selectedTab = AppTab.Tutor },\n                    onSettings = { showSettings = true }\n                )',
    1,
)

s = s.replace(
    '    onWrite: () -> Unit,\n    onTutor: () -> Unit\n) {',
    '    onWrite: () -> Unit,\n    onTutor: () -> Unit,\n    onSettings: () -> Unit\n) {',
    1,
)

s = s.replace(
    '        Text("HANGEUL AI", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)\n        Spacer(Modifier.height(6.dp))',
    '        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {\n            Text("HANGEUL AI", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)\n            Spacer(Modifier.weight(1f))\n            Text("⚙", fontSize = 20.sp, modifier = Modifier.clickable { onSettings() }.padding(8.dp))\n        }\n        Spacer(Modifier.height(6.dp))',
    1,
)

s = s.replace(
    '    var rate by remember { mutableStateOf(1.0f) }',
    '    val prefs = remember { context.getSharedPreferences("hangeul_ai", 0) }\n    val rate = prefs.getFloat("tts_rate", 1.0f)',
    1,
)

s = s.replace(
    '                val voice = engineRef?.voices?.filter { it.locale.language == "ko" && !it.isNetworkConnectionRequired }?.maxByOrNull { it.quality }\n                if (voice != null) engineRef?.voice = voice',
    '                val voices = engineRef?.voices?.filter { it.locale.language == "ko" } ?: emptySet()\n                val savedVoice = prefs.getString("tts_voice", null)\n                val voice = voices.firstOrNull { it.name == savedVoice } ?: voices.filter { !it.isNetworkConnectionRequired }.maxByOrNull { it.quality } ?: voices.firstOrNull()\n                if (voice != null) engineRef?.voice = voice',
    1,
)

rate_controls = '''                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = rate == 0.7f, onClick = { rate = 0.7f }, label = { Text("0.7× 천천히") })
                    FilterChip(selected = rate == 1.0f, onClick = { rate = 1.0f }, label = { Text("1.0× 일반") })
                }
                Spacer(Modifier.height(10.dp))'''
s = s.replace(rate_controls, '                Spacer(Modifier.height(10.dp))', 1)

settings = r'''

@Composable
private fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("hangeul_ai", 0) }
    var voices by remember { mutableStateOf<List<Voice>>(emptyList()) }
    var selectedVoice by remember { mutableStateOf(prefs.getString("tts_voice", null)) }
    var rate by remember { mutableStateOf(prefs.getFloat("tts_rate", 1.0f)) }
    var previewTts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var ref: TextToSpeech? = null
        val listener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                ref?.language = Locale.KOREAN
                voices = ref?.voices
                    ?.filter { it.locale.language == "ko" }
                    ?.sortedWith(compareBy<Voice> { it.isNetworkConnectionRequired }.thenByDescending { it.quality })
                    ?: emptyList()
            }
        }
        val hasGoogle = runCatching {
            context.packageManager.getApplicationInfo("com.google.android.tts", 0)
        }.isSuccess
        val engine = if (hasGoogle) {
            TextToSpeech(context, listener, "com.google.android.tts")
        } else {
            TextToSpeech(context, listener)
        }
        ref = engine
        previewTts = engine
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) { Text("← 돌아가기", color = Ink) }
        Spacer(Modifier.height(8.dp))
        Text("설정", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Text("듣기 설정만 간단하게 바꿀 수 있어요.", color = Muted)
        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("한국어 음성", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                voices.take(4).forEachIndexed { index, voice ->
                    FilterChip(
                        selected = selectedVoice == voice.name,
                        onClick = {
                            selectedVoice = voice.name
                            previewTts?.voice = voice
                            prefs.edit().putString("tts_voice", voice.name).apply()
                            previewTts?.setSpeechRate(rate)
                            previewTts?.speak(
                                "안녕하세요. 한국어를 같이 배워요.",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "preview"
                            )
                        },
                        label = {
                            Text("음성 ${index + 1}" + if (voice.isNetworkConnectionRequired) " · 온라인" else "")
                        }
                    )
                }

                Spacer(Modifier.height(18.dp))
                Text("말하기 속도", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = rate == 0.7f,
                        onClick = {
                            rate = 0.7f
                            prefs.edit().putFloat("tts_rate", rate).apply()
                        },
                        label = { Text("천천히") }
                    )
                    FilterChip(
                        selected = rate == 1.0f,
                        onClick = {
                            rate = 1.0f
                            prefs.edit().putFloat("tts_rate", rate).apply()
                        },
                        label = { Text("일반") }
                    )
                }
            }
        }
    }
}
'''

marker = '\n@Composable\nprivate fun WritingScreen'
if 'private fun SettingsScreen(' not in s:
    if marker not in s:
        raise SystemExit('WritingScreen marker missing')
    s = s.replace(marker, settings + marker, 1)

p.write_text(s)

from pathlib import Path

p = Path('android/app/src/main/java/com/manso/hangeulai/MainActivity.kt')
s = p.read_text()

s = s.replace(
    '    val rate = prefs.getFloat("tts_rate", 1.0f)\n',
    '    val rate = prefs.getFloat("tts_rate", 0.9f)\n    val pitch = prefs.getFloat("tts_pitch", 0.88f)\n',
    1,
)
s = s.replace(
    '                        tts?.setSpeechRate(rate)\n                        tts?.speak(lesson.korean, TextToSpeech.QUEUE_FLUSH, null, "lesson")',
    '                        tts?.setSpeechRate(rate)\n                        tts?.setPitch(pitch)\n                        tts?.speak(lesson.korean, TextToSpeech.QUEUE_FLUSH, null, "lesson")',
    1,
)
s = s.replace(
    '    var rate by remember { mutableStateOf(prefs.getFloat("tts_rate", 1.0f)) }\n',
    '    var rate by remember { mutableStateOf(prefs.getFloat("tts_rate", 0.9f)) }\n    var pitch by remember { mutableStateOf(prefs.getFloat("tts_pitch", 0.88f)) }\n',
    1,
)
s = s.replace(
    '''                voices = ref?.voices
                    ?.filter { it.locale.language == "ko" }
                    ?.sortedWith(compareBy<Voice> { it.isNetworkConnectionRequired }.thenByDescending { it.quality })
                    ?: emptyList()''',
    '''                val allKoreanVoices = ref?.voices
                    ?.filter { it.locale.language == "ko" }
                    ?.sortedWith(compareBy<Voice> { it.isNetworkConnectionRequired }.thenByDescending { it.quality })
                    ?: emptyList()
                voices = allKoreanVoices.distinctBy { voice ->
                    voice.name
                        .lowercase()
                        .replace(Regex("(network|local|offline|online|legacy|enhanced|compact|downloaded)"), "")
                        .replace(Regex("[^a-z0-9가-힣]"), "")
                }''',
    1,
)
s = s.replace(
    '                            previewTts?.setSpeechRate(rate)\n                            previewTts?.speak(',
    '                            previewTts?.setSpeechRate(rate)\n                            previewTts?.setPitch(pitch)\n                            previewTts?.speak(',
    1,
)
s = s.replace(
    '                voices.take(4).forEachIndexed { index, voice ->',
    '                voices.take(3).forEachIndexed { index, voice ->',
    1,
)
s = s.replace(
    '                            rate = 1.0f\n                            prefs.edit().putFloat("tts_rate", rate).apply()',
    '                            rate = 0.9f\n                            prefs.edit().putFloat("tts_rate", rate).apply()',
    1,
)
s = s.replace(
    '                        selected = rate == 1.0f,',
    '                        selected = rate == 0.9f,',
    1,
)
needle = '''                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = rate == 0.7f,'''
# pitch controls go after the speed Row; use stable closing fragment near SettingsScreen end.
fragment = '''                    )
                }
            }
        }
    }
}

@Composable
private fun WritingScreen'''
replacement = '''                    )
                }

                Spacer(Modifier.height(18.dp))
                Text("목소리 톤", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = pitch == 0.88f,
                        onClick = {
                            pitch = 0.88f
                            prefs.edit().putFloat("tts_pitch", pitch).apply()
                            previewTts?.setPitch(pitch)
                            previewTts?.setSpeechRate(rate)
                            previewTts?.speak("안녕하세요. 한국어를 같이 배워요.", TextToSpeech.QUEUE_FLUSH, null, "pitch")
                        },
                        label = { Text("낮게") }
                    )
                    FilterChip(
                        selected = pitch == 1.0f,
                        onClick = {
                            pitch = 1.0f
                            prefs.edit().putFloat("tts_pitch", pitch).apply()
                            previewTts?.setPitch(pitch)
                            previewTts?.setSpeechRate(rate)
                            previewTts?.speak("안녕하세요. 한국어를 같이 배워요.", TextToSpeech.QUEUE_FLUSH, null, "pitch")
                        },
                        label = { Text("기본") }
                    )
                }
            }
        }
    }
}

@Composable
private fun WritingScreen'''
if fragment not in s:
    raise SystemExit('settings end marker missing')
s = s.replace(fragment, replacement, 1)
p.write_text(s)

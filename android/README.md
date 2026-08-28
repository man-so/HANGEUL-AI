# Hangeul AI Android MVP

첫 번째 End-to-End Android MVP입니다.

## 구현된 기능

- Jetpack Compose 기반 모바일 UI
- 오늘의 한국어 문장 카드
- 영어 번역과 핵심 어휘
- Android TextToSpeech 기반 한국어 듣기
- 0.7x / 일반 속도 선택
- SharedPreferences 기반 문장 저장
- 손가락/스타일러스 한글 따라쓰기 Canvas
- 학습 → 쓰기 → AI 튜터 Bottom Navigation
- AI Tutor Provider 교체 전 흐름 검증용 Mock AI

## 현재 학습 문장

`시작이 반이다.`

- Translation: Well begun is half done.
- Level: A1
- Vocabulary: 시작 / 반 / 이다

## 실행

1. Android Studio 최신 Stable 버전으로 `android` 폴더를 엽니다.
2. Android SDK API 37을 설치합니다.
3. Gradle Sync를 실행합니다.
4. Android 8.0(API 26) 이상 Emulator 또는 실제 기기에서 실행합니다.

## 기술 기준

- Android Gradle Plugin 9.3.0
- Kotlin 2.3.20
- compileSdk 37
- targetSdk 36
- Jetpack Compose BOM 2026.08.00
- Java 17

## 다음 단계

### Phase 2 — Content
- 다수의 한국어 명언/문장 데이터
- A1/A2 레벨
- 학습 언어 영어/한국어 전환
- 저장 문장 목록과 복습

### Phase 3 — On-device AI
- `AITutorProvider` 추상화
- GemmaOnDeviceProvider
- 문법 설명
- 예문 생성
- 문장 교정
- 퀴즈 생성

### Phase 4 — Speaking
- STT
- 따라 말하기
- 발음 연습

> 실제 Gemma 연결 전에는 Mock AI로 UI와 전체 학습 Workflow를 먼저 검증합니다.

package com.manso.hangeulai

data class GrammarPoint(
    val pattern: String,
    val meaning: String
)

val lessonCatalog = listOf(
    Lesson(
        id = "mudo_001",
        collection = "무한도전으로 배우는 한국어",
        speaker = "박명수",
        korean = "티끌 모아봤자 티끌이다.",
        simpleKorean = "아주 작은 것을 계속 모아도 여전히 작다는 뜻으로 하는 농담이에요.",
        translation = "Even if you gather tiny bits, they're still tiny.",
        japanese = "ちりを集めても、しょせんちりだ。",
        grammar = listOf(GrammarPoint("-아/어 봤자", "어떤 행동을 해도 기대하는 좋은 결과가 나오기 어렵다는 뜻이에요.")),
        example = "지금 출발해 봤자 이미 늦었어요.",
        level = "B1",
        category = listOf("예능", "유머", "현실"),
        sourceType = "famous_quote",
        explanation = "작은 것을 아무리 모아도 여전히 작다는 뜻을 유머러스하게 표현한 문장이에요.",
        vocabulary = listOf("티끌" to "tiny speck · dust", "모으다" to "to gather", "-아/어 봤자" to "even if · no use")
    ),
    Lesson(
        id = "mudo_002", collection = "무한도전으로 배우는 한국어", speaker = "박명수",
        korean = "시작은 반이 아니다. 시작일 뿐이다.",
        simpleKorean = "시작했다고 일이 많이 끝난 것은 아니라는 재미있는 표현이에요.",
        translation = "Starting isn't half the battle. It's only the beginning.",
        japanese = "始めたからといって半分終わったわけではない。ただ始めただけだ。",
        grammar = listOf(GrammarPoint("-일 뿐이다", "그것 이외에는 특별한 것이 없다는 뜻이에요.")),
        example = "이건 연습일 뿐이에요.", level = "B1", category = listOf("예능", "도전", "유머"), sourceType = "famous_quote",
        explanation = "시작 자체보다 실제로 계속 행동하는 것이 중요하다는 뜻을 익살스럽게 강조해요.",
        vocabulary = listOf("시작" to "start · beginning", "반" to "half", "뿐" to "only · merely")
    ),
    Lesson(
        id = "mudo_003", collection = "무한도전으로 배우는 한국어", speaker = "박명수",
        korean = "늦었다고 생각할 때는 이미 늦었다. 당장 시작해라.",
        simpleKorean = "계속 고민하면서 미루지 말고 지금 바로 시작하라는 뜻이에요.",
        translation = "When you think it's too late, it probably already is. Start now.",
        japanese = "遅いと思った時には、もう遅い。今すぐ始めろ。",
        grammar = listOf(
            GrammarPoint("-다고 생각하다", "자신의 생각이나 판단을 표현할 때 사용해요."),
            GrammarPoint("-아/어라", "상대방에게 어떤 행동을 하라고 강하게 말하는 표현이에요.")
        ),
        example = "어렵다고 생각하지 말고 한번 해 봐요.", level = "B1", category = listOf("예능", "도전", "동기부여"), sourceType = "famous_quote",
        explanation = "미루지 말고 지금 행동하라는 강한 조언이에요.",
        vocabulary = listOf("늦다" to "to be late", "당장" to "right now", "시작하다" to "to start")
    ),
    Lesson(
        id = "mudo_004", collection = "무한도전으로 배우는 한국어", speaker = "학습용",
        korean = "해 보기 전에는 모른다.", simpleKorean = "직접 해 보지 않으면 결과를 알 수 없다는 뜻이에요.",
        translation = "You won't know until you try.", japanese = "やってみるまでは分からない。",
        grammar = listOf(GrammarPoint("-기 전에는", "어떤 행동이나 일이 일어나기 이전을 나타내요.")),
        example = "직접 먹어 보기 전에는 맛을 몰라요.", level = "A2", category = listOf("도전", "일상"), sourceType = "learning_recreation",
        explanation = "직접 경험하기 전에는 결과를 단정할 수 없다는 뜻이에요.",
        vocabulary = listOf("해 보다" to "to try doing", "전" to "before", "모르다" to "not know")
    ),
    Lesson(
        id = "mudo_005", collection = "무한도전으로 배우는 한국어", speaker = "학습용",
        korean = "실패해도 다시 하면 된다.", simpleKorean = "한 번 실패했다고 포기할 필요는 없다는 뜻이에요.",
        translation = "Even if you fail, you can try again.", japanese = "失敗しても、またやればいい。",
        grammar = listOf(GrammarPoint("-아/어도", "앞의 상황이 생겨도 뒤의 내용에는 영향을 주지 않는다는 뜻이에요."), GrammarPoint("-으면 되다", "그렇게 하는 것으로 충분하다는 뜻이에요.")),
        example = "틀려도 다시 하면 돼요.", level = "A2", category = listOf("도전", "응원"), sourceType = "learning_recreation",
        explanation = "실패해도 포기하지 않고 다시 시도할 수 있다는 응원의 문장이에요.",
        vocabulary = listOf("실패하다" to "to fail", "다시" to "again", "되다" to "to be okay · work")
    ),
    Lesson(
        id = "mudo_006", collection = "무한도전으로 배우는 한국어", speaker = "학습용",
        korean = "모르면 물어보면 된다.", simpleKorean = "모르는 것이 있으면 혼자 고민하지 말고 질문하라는 뜻이에요.",
        translation = "If you don't know, just ask.", japanese = "分からなければ、聞けばいい。",
        grammar = listOf(GrammarPoint("-으면 되다", "어떤 문제를 해결하는 간단한 방법을 말할 때 사용할 수 있어요.")),
        example = "길을 모르면 사람들에게 물어보면 돼요.", level = "A2", category = listOf("일상", "대화"), sourceType = "learning_recreation",
        explanation = "모르는 것을 질문하는 것이 자연스럽고 좋은 해결 방법이라는 뜻이에요.",
        vocabulary = listOf("모르다" to "not know", "물어보다" to "to ask", "되다" to "to be enough")
    ),
    Lesson(
        id = "mudo_007", collection = "무한도전으로 배우는 한국어", speaker = "학습용",
        korean = "생각대로 되는 일이 별로 없다.", simpleKorean = "현실에서는 우리가 계획한 대로 되지 않는 일이 많다는 뜻이에요.",
        translation = "Things rarely go exactly as planned.", japanese = "思い通りになることはあまりない。",
        grammar = listOf(GrammarPoint("-대로", "어떤 방법이나 생각과 똑같이 이루어진다는 뜻이에요."), GrammarPoint("별로 -지 않다", "많지 않거나 정도가 크지 않다는 뜻이에요.")),
        example = "여행이 계획대로 되지 않았어요.", level = "B1", category = listOf("현실", "일상"), sourceType = "learning_recreation",
        explanation = "현실은 계획과 다르게 흘러갈 수 있다는 뜻이에요.",
        vocabulary = listOf("생각대로" to "as one thinks", "별로" to "not particularly · not much", "일" to "thing · matter")
    ),
    Lesson(
        id = "mudo_008", collection = "무한도전으로 배우는 한국어", speaker = "학습용",
        korean = "힘들어도 끝까지 한번 해 보자.", simpleKorean = "어려운 상황에서도 포기하지 말고 계속해 보자는 뜻이에요.",
        translation = "Even if it's hard, let's see it through.", japanese = "大変でも、最後までやってみよう。",
        grammar = listOf(GrammarPoint("-아/어도", "어떤 상황이 있어도 뒤의 행동을 계속한다는 뜻이에요."), GrammarPoint("-아/어 보자", "다른 사람과 함께 어떤 행동을 시도하자고 제안하는 표현이에요.")),
        example = "어려워도 끝까지 공부해 봐요.", level = "A2", category = listOf("도전", "응원"), sourceType = "learning_recreation",
        explanation = "힘든 상황에서도 끝까지 시도해 보자는 제안이에요.",
        vocabulary = listOf("힘들다" to "to be hard", "끝까지" to "until the end", "해 보다" to "to try")
    ),
    Lesson(
        id = "mudo_009", collection = "무한도전으로 배우는 한국어", speaker = "학습용",
        korean = "같이 하면 어려운 일도 재미있어진다.", simpleKorean = "힘든 일도 다른 사람과 함께하면 즐거울 수 있다는 뜻이에요.",
        translation = "Even difficult things become fun when we do them together.", japanese = "一緒にやれば、大変なことも楽しくなる。",
        grammar = listOf(GrammarPoint("-(으)면", "어떤 조건이 생겼을 때의 결과를 말해요."), GrammarPoint("-아/어지다", "상태가 이전과 다르게 변하는 것을 나타내요.")),
        example = "날씨가 점점 따뜻해져요.", level = "A2", category = listOf("친구", "도전", "일상"), sourceType = "learning_recreation",
        explanation = "함께하면 어려운 일도 더 즐겁게 느껴질 수 있다는 뜻이에요.",
        vocabulary = listOf("같이" to "together", "어렵다" to "to be difficult", "재미있어지다" to "to become fun")
    ),
    Lesson(
        id = "mudo_010", collection = "무한도전으로 배우는 한국어", speaker = "학습용",
        korean = "중요한 건 포기하지 않는 것이다.", simpleKorean = "잘하지 못하더라도 계속 노력하는 것이 중요하다는 뜻이에요.",
        translation = "What matters is not giving up.", japanese = "大切なのは諦めないことだ。",
        grammar = listOf(GrammarPoint("중요한 건 -는 것이다", "여러 가지 중에서 가장 중요한 내용을 강조할 때 사용할 수 있어요.")),
        example = "중요한 건 매일 조금씩 공부하는 거예요.", level = "B1", category = listOf("도전", "응원"), sourceType = "learning_recreation",
        explanation = "완벽하게 잘하는 것보다 포기하지 않고 계속하는 것이 중요하다는 뜻이에요.",
        vocabulary = listOf("중요하다" to "to be important", "포기하다" to "to give up", "것" to "thing · fact")
    )
)

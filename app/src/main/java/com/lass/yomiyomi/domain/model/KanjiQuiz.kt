package com.lass.yomiyomi.domain.model

data class KanjiQuiz(
    val kanji: String,
    val correctString: String,
    val optionStrings: List<String>, // 정답 포함
    val correctIndex: Int // 정답 인덱스
)
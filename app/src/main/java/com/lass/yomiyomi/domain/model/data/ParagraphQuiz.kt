package com.lass.yomiyomi.domain.model.data

data class ParagraphQuiz(
    val paragraphId: String,          // 문단 ID
    val title: String,                // 문단 제목
    val originalText: String,         // 원본 일본어 텍스트 (후리가나 포함)
    val displayText: String,          // 빈칸이 있는 표시용 텍스트
    val koreanText: String,           // 한국어 번역
    val blanks: List<BlankItem>,      // 빈칸 정보 리스트
    val filledBlanks: MutableMap<Int, String> = mutableMapOf() // 채워진 빈칸들 (인덱스 -> 답)
)

data class BlankItem(
    val index: Int,                   // 빈칸 순서 (0부터 시작)
    val correctAnswer: String,        // 정답 (요미가나)
    val position: IntRange            // 원본 텍스트에서의 위치
) 
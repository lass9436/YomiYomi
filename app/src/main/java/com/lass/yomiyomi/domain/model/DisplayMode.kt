package com.lass.yomiyomi.domain.model

// 표시 모드 열거형
enum class DisplayMode {
    FULL,           // 전체 표시 (한자 + 요미가나 + 히라가나)
    JAPANESE_ONLY,  // 일본어만 (한자 + 히라가나, 요미가나 숨김)
    FURIGANA_ONLY,  // 요미가나만 (한자 숨김)
    KANJI_ONLY      // 한자만 (요미가나 + 히라가나 숨김)
} 
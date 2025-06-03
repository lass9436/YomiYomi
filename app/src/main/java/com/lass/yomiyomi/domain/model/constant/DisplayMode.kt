package com.lass.yomiyomi.domain.model.constant

// 표시 모드 열거형
enum class DisplayMode {
    FULL,                   // 전체 표시 (일본어 + 한국어 번역)
    JAPANESE_ONLY,          // 일본어만 (한국어 번역 숨김)
    JAPANESE_NO_FURIGANA,   // 요미가나 없이 (한자만, 읽기 연습용)
    KOREAN_ONLY             // 한국어만 (일본어 숨김)
} 

package com.lass.yomiyomi.util

import com.lass.yomiyomi.domain.model.data.TextSegment

object FuriganaParser {
    fun parse(text: String): List<TextSegment> {
        val segments = mutableListOf<TextSegment>()
        val pattern = """([^[\]]+)(?:\[([^\]]+)\])?""".toRegex()
        
        pattern.findAll(text).forEach { match ->
            val baseText = match.groupValues[1]
            val furigana = match.groupValues[2].takeIf { it.isNotEmpty() }
            segments.add(TextSegment(baseText, furigana))
        }
        
        return segments
    }
    
    // 한자인지 판별 (간단한 유니코드 범위 체크)
    fun isKanji(char: Char): Boolean {
        return char.code in 0x4E00..0x9FAF || // CJK Unified Ideographs
               char.code in 0x3400..0x4DBF    // CJK Extension A
    }
    
    fun hasKanji(text: String): Boolean {
        return text.any { isKanji(it) }
    }
} 

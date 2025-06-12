package com.lass.yomiyomi.util

import com.lass.yomiyomi.domain.model.data.TextSegment

object FuriganaParser {
    fun parse(text: String): List<TextSegment> {
        val segments = mutableListOf<TextSegment>()
        var i = 0
        while (i < text.length) {
            val kanjiStart = i
            var foundKanjiWithFurigana = false
            val result = parseKanjiWithFurigana(text, i, segments)
            i = result.first
            foundKanjiWithFurigana = result.second
            if (!foundKanjiWithFurigana) {
                i = parseNormalText(text, kanjiStart, segments)
            }
        }
        return segments
    }
    
    private fun parseKanjiWithFurigana(text: String, start: Int, segments: MutableList<TextSegment>): Pair<Int, Boolean> {
        var i = start
        while (i < text.length && isKanji(text[i])) {
            i++
        }
        if (i > start && i < text.length && text[i] == '[') {
            val furiganaStart = i + 1
            val furiganaEnd = text.indexOf(']', furiganaStart)
            if (furiganaEnd != -1) {
                val kanjiText = text.substring(start, i)
                val furigana = text.substring(furiganaStart, furiganaEnd)
                segments.add(TextSegment(kanjiText, furigana))
                return Pair(furiganaEnd + 1, true)
            }
        }
        return Pair(start, false)
    }
    
    private fun parseNormalText(text: String, start: Int, segments: MutableList<TextSegment>): Int {
        var i = start
        while (i < text.length) {
            if (isKanji(text[i])) {
                val nextKanjiStart = i
                while (i < text.length && isKanji(text[i])) {
                    i++
                }
                if (i < text.length && text[i] == '[') {
                    val nextFuriganaEnd = text.indexOf(']', i)
                    if (nextFuriganaEnd != -1) {
                        i = nextKanjiStart
                        break
                    }
                }
            } else {
                i++
            }
        }
        if (i > start) {
            val normalText = text.substring(start, i)
            normalText.forEach { c ->
                segments.add(TextSegment(c.toString(), null))
            }
        }
        return i
    }
    
    // 한자인지 판별 (간단한 유니코드 범위 체크)
    fun isKanji(char: Char): Boolean {
        return char.code in 0x4E00..0x9FAF || // CJK Unified Ideographs
               char.code in 0x3400..0x4DBF || // CJK Extension A
               char.code == 0x3005             // 々 (반복 기호)
    }

    // 가나(히라가나/가타카나)인지 판별
    fun isKana(char: Char): Boolean {
        return (char.code in 0x3040..0x309F) || // 히라가나
               (char.code in 0x30A0..0x30FF)    // 가타카나
    }
    
    fun hasKanji(text: String): Boolean {
        return text.any { isKanji(it) }
    }
} 

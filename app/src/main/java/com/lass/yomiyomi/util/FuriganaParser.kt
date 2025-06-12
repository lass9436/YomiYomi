package com.lass.yomiyomi.util

import com.lass.yomiyomi.domain.model.data.TextSegment

object FuriganaParser {
    fun parse(text: String): List<TextSegment> {
        val segments = mutableListOf<TextSegment>()
        var i = 0
        
        while (i < text.length) {
            // 한자[후리가나] 패턴을 찾는다
            val kanjiStart = i
            var foundKanjiWithFurigana = false
            
            // 연속된 한자 찾기
            while (i < text.length && isKanji(text[i])) {
                i++
            }
            
            // 한자 다음에 [후리가나]가 있는지 확인
            if (i > kanjiStart && i < text.length && text[i] == '[') {
                val furiganaStart = i + 1
                val furiganaEnd = text.indexOf(']', furiganaStart)
                
                if (furiganaEnd != -1) {
                    // 한자[후리가나] 패턴 발견!
                    val kanjiText = text.substring(kanjiStart, i)
                    val furigana = text.substring(furiganaStart, furiganaEnd)
                    segments.add(TextSegment(kanjiText, furigana))
                    i = furiganaEnd + 1
                    foundKanjiWithFurigana = true
                }
            }
            
            if (!foundKanjiWithFurigana) {
                // 한자[후리가나] 패턴이 아닌 경우, 연속된 일반 텍스트로 처리
                val normalTextStart = kanjiStart
                i = kanjiStart
                
                // 다음 한자[후리가나] 패턴이 나올 때까지 계속 읽기
                while (i < text.length) {
                    if (isKanji(text[i])) {
                        // 한자 시작점 찾기
                        val nextKanjiStart = i
                        while (i < text.length && isKanji(text[i])) {
                            i++
                        }
                        // 다음이 [후리가나]인지 확인
                        if (i < text.length && text[i] == '[') {
                            val nextFuriganaEnd = text.indexOf(']', i)
                            if (nextFuriganaEnd != -1) {
                                // 다음 한자[후리가나] 패턴 발견, 여기서 멈춤
                                i = nextKanjiStart
                                break
                            }
                        }
                        // 한자[후리가나] 패턴이 아니면 계속 진행
                    } else {
                        i++
                    }
                }
                if (i > normalTextStart) {
                    val normalText = text.substring(normalTextStart, i)
                    // 모든 일반 텍스트(가나, 숫자, 기타) 무조건 한 글자씩 쪼갬
                    normalText.forEach { c ->
                        segments.add(TextSegment(c.toString(), null))
                    }
                }
            }
        }
        
        return segments
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

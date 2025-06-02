package com.lass.yomiyomi.util

/**
 * 일본어 텍스트 필터링 유틸리티
 * 한자, 히라가나, 카타카나만 추출하고 한글은 제외
 */
object JapaneseTextFilter {
    
    /**
     * 텍스트에서 일본어 문자만 추출
     * @param text 원본 텍스트
     * @return 한자, 히라가나, 카타카나만 포함된 텍스트
     */
    fun extractJapaneseOnly(text: String): String {
        return text.filter { char ->
            isJapaneseCharacter(char)
        }
    }
    
    /**
     * 문자가 일본어 문자인지 확인
     * @param char 확인할 문자
     * @return 일본어 문자 여부
     */
    private fun isJapaneseCharacter(char: Char): Boolean {
        val code = char.code
        return isHiragana(code) || isKatakana(code) || isKanji(code) || isJapanesePunctuation(code)
    }
    
    /**
     * 히라가나 범위 확인 (ひらがな)
     * Unicode 범위: U+3040–U+309F
     */
    private fun isHiragana(code: Int): Boolean {
        return code in 0x3040..0x309F
    }
    
    /**
     * 카타카나 범위 확인 (カタカナ)
     * Unicode 범위: U+30A0–U+30FF
     */
    private fun isKatakana(code: Int): Boolean {
        return code in 0x30A0..0x30FF
    }
    
    /**
     * 한자 범위 확인 (漢字)
     * Unicode 범위: U+4E00–U+9FAF (CJK Unified Ideographs)
     */
    private fun isKanji(code: Int): Boolean {
        return code in 0x4E00..0x9FAF
    }
    
    /**
     * 일본어 문장부호 확인
     * 일부 일본어 문장부호들 포함
     */
    private fun isJapanesePunctuation(code: Int): Boolean {
        return when (code) {
            0x3001, // 、(일본어 쉼표)
            0x3002, // 。(일본어 마침표)
            0x300C, // 「(일본어 여는 따옴표)
            0x300D, // 」(일본어 닫는 따옴표)
            0x300E, // 『(일본어 여는 겹따옴표)
            0x300F, // 』(일본어 닫는 겹따옴표)
            0x3010, // 【(일본어 여는 대괄호)
            0x3011, // 】(일본어 닫는 대괄호)
            0x30FB, // ・(일본어 중점)
            0x30FC, // ー(일본어 장음표)
            0xFF01, // ！(전각 느낌표)
            0xFF1F  // ？(전각 물음표)
            -> true
            else -> false
        }
    }
    
    /**
     * 텍스트가 일본어를 포함하고 있는지 확인
     * @param text 확인할 텍스트
     * @return 일본어 포함 여부
     */
    fun containsJapanese(text: String): Boolean {
        return text.any { char -> isJapaneseCharacter(char) }
    }
    
    /**
     * TTS용 텍스트 정리
     * 일본어만 추출하고 공백 정리
     * @param text 원본 텍스트
     * @return TTS에 적합한 정리된 일본어 텍스트
     */
    fun prepareTTSText(text: String): String {
        val japaneseOnly = extractJapaneseOnly(text)
        return japaneseOnly.trim().takeIf { it.isNotEmpty() } ?: ""
    }
} 
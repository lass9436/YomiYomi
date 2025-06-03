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
     * 문자가 일본어 문자인지 확인 (TTS용 공백, 쉼표 포함)
     * @param char 확인할 문자
     * @return 일본어 문자 여부
     */
    private fun isJapaneseCharacter(char: Char): Boolean {
        val code = char.code
        return isHiragana(code) || isKatakana(code) || isKanji(code) || isJapanesePunctuation(code) || 
               char == ' ' || char == ','  // TTS pause를 위한 공백과 영어 쉼표 허용
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
     * 후리가나 대괄호 제거
     * "私[わたし]は学生[がくせい]です" → "私は学生です"
     * @param text 후리가나가 포함된 텍스트
     * @return 후리가나가 제거된 텍스트
     */
    fun removeFurigana(text: String): String {
        return text.replace(Regex("\\[.*?\\]"), "")
    }
    
    /**
     * TTS를 위한 쉼표 정규화
     * 일본어 쉼표(、)를 영어 쉼표 + 공백(, )으로 변환하여 TTS가 pause를 인식하도록 함
     * @param text 원본 텍스트
     * @return 정규화된 텍스트
     */
    fun normalizeCommasForTTS(text: String): String {
        return text.replace("、", ", ").replace(",", ", ").replace("  ", " ")
    }
    
    /**
     * TTS용 텍스트 정리
     * 1. 후리가나 대괄호 제거
     * 2. 쉼표 정규화 (pause 인식을 위해)
     * 3. 일본어만 추출
     * @param text 원본 텍스트
     * @return TTS에 적합한 정리된 일본어 텍스트
     */
    fun prepareTTSText(text: String): String {
        val withoutFurigana = removeFurigana(text)
        val normalizedCommas = normalizeCommasForTTS(withoutFurigana)
        val japaneseOnly = extractJapaneseOnly(normalizedCommas)
        return japaneseOnly.trim().takeIf { it.isNotEmpty() } ?: ""
    }
} 
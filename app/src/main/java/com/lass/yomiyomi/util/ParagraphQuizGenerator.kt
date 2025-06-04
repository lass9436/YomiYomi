package com.lass.yomiyomi.util

import com.lass.yomiyomi.domain.model.data.BlankItem
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.domain.model.constant.ParagraphQuizType

object ParagraphQuizGenerator {
    
    /**
     * 후리가나가 포함된 텍스트에서 빈칸 퀴즈를 생성
     * @param paragraphId 문단 ID
     * @param paragraphTitle 문단 제목
     * @param japaneseText 후리가나가 포함된 일본어 텍스트
     * @param koreanText 한국어 번역
     * @param quizType 퀴즈 타입 (현재는 사용하지 않음)
     * @return ParagraphQuiz 객체
     */
    fun generateParagraphQuiz(
        paragraphId: String,
        paragraphTitle: String,
        japaneseText: String,
        koreanText: String,
        quizType: ParagraphQuizType
    ): ParagraphQuiz {
        val blanks = mutableListOf<BlankItem>()
        val furiganaPattern = Regex("\\[([^\\]]+)\\]")
        var blankIndex = 0
        
        // 후리가나 패턴을 찾아서 BlankItem 생성
        furiganaPattern.findAll(japaneseText).forEach { matchResult ->
            val furigana = matchResult.groupValues[1] // [わたし] -> わたし
            val range = matchResult.range
            
            blanks.add(
                BlankItem(
                    index = blankIndex++,
                    correctAnswer = furigana,
                    position = range
                )
            )
        }
        
        // 원본 텍스트를 그대로 사용 (치환하지 않음)
        val displayText = japaneseText
        
        return ParagraphQuiz(
            paragraphId = paragraphId,
            title = paragraphTitle,
            originalText = japaneseText,
            displayText = displayText,
            koreanText = koreanText,
            blanks = blanks
        )
    }
    
    /**
     * 현재 표시할 텍스트 생성 (채워진 빈칸들을 반영)
     * @param quiz 현재 퀴즈 객체
     * @return 업데이트된 표시용 텍스트 (원본 그대로, UI에서 처리)
     */
    fun getDisplayTextWithFilledBlanks(quiz: ParagraphQuiz): String {
        // 원본 텍스트 그대로 반환, UI에서 빈칸 처리
        return quiz.originalText
    }
    
    /**
     * 퀴즈 완료 여부 확인
     * @param quiz 현재 퀴즈 객체
     * @return 모든 빈칸이 채워졌는지 여부
     */
    fun isQuizCompleted(quiz: ParagraphQuiz): Boolean {
        return quiz.blanks.size == quiz.filledBlanks.size
    }
    
    /**
     * 진행률 계산
     * @param quiz 현재 퀴즈 객체
     * @return 0.0 ~ 1.0 사이의 진행률
     */
    fun getProgress(quiz: ParagraphQuiz): Float {
        if (quiz.blanks.isEmpty()) return 0f
        return quiz.filledBlanks.size.toFloat() / quiz.blanks.size.toFloat()
    }
    
    /**
     * 음성 인식된 텍스트에서 정답을 찾아서 빈칸을 채움
     * @param quiz 현재 퀴즈 객체
     * @param recognizedText 음성 인식된 텍스트
     * @return 새로 채워진 빈칸들의 정답 리스트
     */
    fun fillBlanks(quiz: ParagraphQuiz, recognizedText: String): List<String> {
        val newlyFilled = mutableListOf<String>()
        
        // 디버그 로그
        println("Debug - Recognized text: '$recognizedText'")
        println("Debug - Original text: '${quiz.originalText}'")
        
        quiz.blanks.forEach { blank ->
            // 이미 채워진 빈칸은 건너뛰기
            if (!quiz.filledBlanks.containsKey(blank.index)) {
                val answer = blank.correctAnswer
                println("Debug - Checking answer: '$answer'")
                
                // 여러 매칭 전략 시도
                val isMatched = tryAllMatchingStrategies(recognizedText, answer, quiz.originalText, blank.position)
                
                if (isMatched) {
                    quiz.filledBlanks[blank.index] = answer
                    newlyFilled.add(answer)
                    println("Debug - ✅ Filled blank: '$answer'")
                } else {
                    println("Debug - ❌ No match for: '$answer'")
                }
            }
        }
        
        return newlyFilled
    }
    
    /**
     * 다양한 매칭 전략으로 음성 인식과 정답을 비교
     */
    private fun tryAllMatchingStrategies(
        recognizedText: String, 
        answer: String, 
        originalText: String,
        answerPosition: IntRange
    ): Boolean {
        println("Debug - Trying strategies for answer: '$answer'")
        
        // 전략 1: 단순 히라가나 포함 확인
        val recognizedHiragana = convertToHiraganaOnly(recognizedText)
        val answerHiragana = convertToHiraganaOnly(answer)
        
        println("Debug - Strategy 1: '$recognizedHiragana' contains '$answerHiragana'?")
        if (recognizedHiragana.contains(answerHiragana)) {
            println("Debug - Strategy 1: ✅ Match found!")
            return true
        }
        
        // 전략 2: 원문에서 해당 위치의 한자 찾기 (숫자 정규화 포함)
        val kanjiAtPosition = extractKanjiAtPosition(originalText, answerPosition)
        if (kanjiAtPosition.isNotEmpty()) {
            println("Debug - Strategy 2: Looking for kanji '$kanjiAtPosition' in '$recognizedText'")
            
            // 2-1: 원래 한자 그대로 찾기
            if (recognizedText.contains(kanjiAtPosition)) {
                println("Debug - Strategy 2-1: ✅ Exact kanji match found!")
                return true
            }
            
            // 2-2: 숫자 정규화해서 찾기
            val normalizedKanji = normalizeNumbers(kanjiAtPosition)
            val normalizedRecognized = normalizeNumbers(recognizedText)
            
            println("Debug - Strategy 2-2: Normalized kanji '$normalizedKanji' in normalized text '$normalizedRecognized'?")
            if (normalizedRecognized.contains(normalizedKanji)) {
                println("Debug - Strategy 2-2: ✅ Normalized number match found!")
                return true
            }
        }
        
        // 전략 3: 정규화된 텍스트로 부분 매칭 (기존 방식)
        val normalizedRecognized = JapaneseTextFilter.normalizeForComparison(recognizedText)
        val normalizedAnswer = JapaneseTextFilter.normalizeForComparison(answer)
        
        println("Debug - Strategy 3: '$normalizedRecognized' contains '$normalizedAnswer'?")
        if (normalizedRecognized.contains(normalizedAnswer)) {
            println("Debug - Strategy 3: ✅ Normalized match found!")
            return true
        }
        
        println("Debug - All strategies failed for: '$answer'")
        return false
    }
    
    /**
     * 텍스트에서 히라가나만 추출
     */
    private fun convertToHiraganaOnly(text: String): String {
        return text.filter { char ->
            val code = char.code
            code in 0x3040..0x309F // 히라가나만
        }
    }
    
    /**
     * 원문에서 특정 위치 앞의 한자를 추출
     * 예: "私[わたし]は" -> position이 [わたし] 위치면 "私" 반환
     */
    private fun extractKanjiAtPosition(originalText: String, bracketPosition: IntRange): String {
        // 대괄호 앞의 한자를 찾기
        var kanjiStart = bracketPosition.first - 1
        
        // 한자 시작점 찾기 (연속된 한자들)
        while (kanjiStart >= 0 && isKanji(originalText[kanjiStart])) {
            kanjiStart--
        }
        kanjiStart++ // 실제 한자 시작점
        
        if (kanjiStart < bracketPosition.first) {
            val kanji = originalText.substring(kanjiStart, bracketPosition.first)
            println("Debug - Found kanji before bracket: '$kanji'")
            return kanji
        }
        
        return ""
    }
    
    /**
     * 한자인지 확인
     */
    private fun isKanji(char: Char): Boolean {
        val code = char.code
        return code in 0x4E00..0x9FAF
    }
    
    /**
     * 텍스트에서 일본어 숫자 한자를 아라비아 숫자로 변환
     * 예: "三時" → "3時", "十二月" → "12月"
     */
    private fun normalizeNumbers(text: String): String {
        val numberMap = mapOf(
            "一" to "1", "二" to "2", "三" to "3", "四" to "4", "五" to "5",
            "六" to "6", "七" to "7", "八" to "8", "九" to "9", "十" to "10",
            "零" to "0", "〇" to "0"
        )
        
        var result = text
        
        // 단순 1:1 변환 먼저 처리
        numberMap.forEach { (kanji, arabic) ->
            result = result.replace(kanji, arabic)
        }
        
        // 복합 숫자 처리 (예: 十一 → 11, 二十 → 20 등)
        result = processComplexNumbers(result)
        
        println("Debug - Number normalization: '$text' → '$result'")
        return result
    }
    
    /**
     * 복합 숫자 처리 (일부만 구현)
     * 예: "10일" → "11", "210" → "20" 등
     */
    private fun processComplexNumbers(text: String): String {
        var result = text
        
        // 십의 자리 처리 (예: 210 → 20, 310 → 30)
        result = result.replace("110", "11")
        result = result.replace("210", "20") 
        result = result.replace("310", "30")
        result = result.replace("410", "40")
        result = result.replace("510", "50")
        result = result.replace("610", "60")
        result = result.replace("710", "70")
        result = result.replace("810", "80")
        result = result.replace("910", "90")
        
        // 10+a 형태 처리 (예: 101 → 11, 102 → 12)
        result = result.replace("101", "11")
        result = result.replace("102", "12")
        result = result.replace("103", "13")
        result = result.replace("104", "14")
        result = result.replace("105", "15")
        result = result.replace("106", "16")
        result = result.replace("107", "17")
        result = result.replace("108", "18")
        result = result.replace("109", "19")
        
        return result
    }
} 
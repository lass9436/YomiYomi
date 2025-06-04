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
        val normalizedRecognized = JapaneseTextFilter.normalizeForComparison(recognizedText)
        
        // 디버그 로그 (개발 중에만 사용)
        println("Debug - Recognized text: '$recognizedText' -> normalized: '$normalizedRecognized'")
        
        quiz.blanks.forEach { blank ->
            // 이미 채워진 빈칸은 건너뛰기
            if (!quiz.filledBlanks.containsKey(blank.index)) {
                val normalizedAnswer = JapaneseTextFilter.normalizeForComparison(blank.correctAnswer)
                
                // 디버그 로그
                println("Debug - Checking answer: '${blank.correctAnswer}' -> normalized: '$normalizedAnswer'")
                
                // 매칭 방식 개선
                val isMatched = when {
                    // 1. 정확히 일치하는 경우 (최우선)
                    normalizedRecognized == normalizedAnswer -> {
                        println("Debug - Exact match found!")
                        true
                    }
                    
                    // 2. 정답이 인식된 텍스트에 단어 경계로 포함된 경우
                    normalizedAnswer.length >= 2 && isWordMatch(normalizedRecognized, normalizedAnswer) -> {
                        println("Debug - Word boundary match found!")
                        true
                    }
                    
                    // 3. 짧은 단어(1글자)는 정확한 매칭만 허용
                    normalizedAnswer.length == 1 && normalizedRecognized.contains(normalizedAnswer) -> {
                        println("Debug - Single character match found!")
                        true
                    }
                    
                    // 4. 기존 contains 방식 (긴 단어만, 더 엄격한 조건)
                    normalizedAnswer.length >= 3 && normalizedRecognized.contains(normalizedAnswer) -> {
                        println("Debug - Contains match found!")
                        true
                    }
                    
                    else -> false
                }
                
                if (isMatched) {
                    quiz.filledBlanks[blank.index] = blank.correctAnswer
                    newlyFilled.add(blank.correctAnswer)
                    println("Debug - Filled blank: '${blank.correctAnswer}'")
                }
            }
        }
        
        return newlyFilled
    }
    
    /**
     * 단어 경계를 고려한 매칭 (간단한 구현)
     * @param text 전체 텍스트
     * @param word 찾을 단어
     * @return 단어 경계로 매칭되는지 여부
     */
    private fun isWordMatch(text: String, word: String): Boolean {
        if (!text.contains(word)) return false
        
        val index = text.indexOf(word)
        val wordEnd = index + word.length
        
        // 단어 앞뒤가 다른 일본어 문자로 둘러싸여 있지 않은지 확인
        val beforeChar = if (index > 0) text[index - 1] else null
        val afterChar = if (wordEnd < text.length) text[wordEnd] else null
        
        // 앞뒤 문자가 일본어가 아니거나 문장의 시작/끝이면 단어 경계로 인정
        val isWordBoundary = (beforeChar == null || !isJapaneseChar(beforeChar)) &&
                            (afterChar == null || !isJapaneseChar(afterChar))
        
        return isWordBoundary
    }
    
    /**
     * 일본어 문자인지 확인 (히라가나, 카타카나, 한자)
     */
    private fun isJapaneseChar(char: Char): Boolean {
        val code = char.code
        return (code in 0x3040..0x309F) || // 히라가나
               (code in 0x30A0..0x30FF) || // 카타카나  
               (code in 0x4E00..0x9FAF)    // 한자
    }
} 
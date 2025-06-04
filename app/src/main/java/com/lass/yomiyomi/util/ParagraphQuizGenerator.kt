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
        
        // 빈칸이 있는 표시용 텍스트 생성 ([わたし] -> [___])
        val displayText = japaneseText.replace(furiganaPattern, "[___]")
        
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
     * @return 업데이트된 표시용 텍스트
     */
    fun getDisplayTextWithFilledBlanks(quiz: ParagraphQuiz): String {
        var displayText = quiz.displayText
        val blankPattern = Regex("\\[___\\]")
        var replacementIndex = 0
        
        return blankPattern.replace(displayText) { _ ->
            val currentBlank = quiz.blanks.getOrNull(replacementIndex)
            val filledAnswer = currentBlank?.let { quiz.filledBlanks[it.index] }
            replacementIndex++
            
            if (filledAnswer != null) {
                "[$filledAnswer]"
            } else {
                "[___]"
            }
        }
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
        
        quiz.blanks.forEach { blank ->
            // 이미 채워진 빈칸은 건너뛰기
            if (!quiz.filledBlanks.containsKey(blank.index)) {
                val normalizedAnswer = JapaneseTextFilter.normalizeForComparison(blank.correctAnswer)
                
                // 음성 인식된 텍스트에 정답이 포함되어 있으면 빈칸 채우기
                if (normalizedRecognized.contains(normalizedAnswer)) {
                    quiz.filledBlanks[blank.index] = blank.correctAnswer
                    newlyFilled.add(blank.correctAnswer)
                }
            }
        }
        
        return newlyFilled
    }
} 
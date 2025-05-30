package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.WordQuiz
import com.lass.yomiyomi.domain.model.WordQuizType

class GenerateWordQuizStudyModeUseCase(
    private val repository: WordRepository,
) {
    // 학습 모드용 데이터 로드
    suspend fun loadLearningModeData(level: Level): Pair<List<Word>, List<Word>> {
        return repository.getWordsForLearningMode(level.toString())
    }

    // 퀴즈 생성
    fun generateQuiz(correctWord: Word, distractors: List<Word>, quizType: WordQuizType): WordQuiz {
        // 오답 3개 선택 (매번 다르게)
        val wrongOptions = distractors.shuffled().take(3)
        
        // 4개의 보기를 만들고 섞기
        val allOptions = (wrongOptions + correctWord).shuffled()
        
        return WordQuiz(
            question = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> correctWord.word
                WordQuizType.MEANING_READING_TO_WORD -> "${correctWord.meaning} / ${correctWord.reading}"
            },
            answer = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> "${correctWord.meaning} / ${correctWord.reading}"
                WordQuizType.MEANING_READING_TO_WORD -> correctWord.word
            },
            options = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> allOptions.map { "${it.meaning} / ${it.reading}" }
                WordQuizType.MEANING_READING_TO_WORD -> allOptions.map { it.word }
            },
            correctIndex = allOptions.indexOf(correctWord)
        )
    }
} 
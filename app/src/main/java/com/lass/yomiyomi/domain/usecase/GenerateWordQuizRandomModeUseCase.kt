package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.WordQuiz
import com.lass.yomiyomi.domain.model.WordQuizType

class GenerateWordQuizRandomModeUseCase(
    private val repository: WordRepository,
) {
    suspend operator fun invoke(level: Level, quizType: WordQuizType): WordQuiz {
        // 1. Word 데이터 가져오기
        val wordList = repository.getAllWordsByLevel(level.toString())

        // 2. 정답 단어 선택
        val correctWord = wordList.random()

        // 3. 랜덤한 오답 선택지 3개 선택
        val shuffledOptions = wordList
            .filter { it != correctWord } // 정답 제외
            .shuffled()
            .take(3)
            .toMutableList()

        // 4. 정답을 옵션에 추가
        if (!shuffledOptions.contains(correctWord)) {
            shuffledOptions.add(correctWord)
        }

        // 5. 옵션 순서 섞기
        shuffledOptions.shuffle()

        return generateQuiz(correctWord, shuffledOptions, quizType)
    }

    private fun generateQuiz(correctWord: Word, allOptions: List<Word>, quizType: WordQuizType): WordQuiz {
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
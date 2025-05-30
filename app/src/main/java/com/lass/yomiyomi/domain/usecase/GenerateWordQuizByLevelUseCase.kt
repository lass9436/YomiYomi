package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.WordQuiz
import com.lass.yomiyomi.domain.model.WordQuizType

class GenerateWordQuizByLevelUseCase(
    private val repository: WordRepository,
) {
    suspend operator fun invoke(level: Level, quizType: WordQuizType, isLearningMode: Boolean): WordQuiz {
        if (isLearningMode) {
            throw IllegalStateException("Learning mode should use loadLearningModeWords and generateQuizFromMemory")
        }

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

        // 6. WordQuizType을 기반으로 퀴즈 데이터 설정
        val question = when (quizType) {
            WordQuizType.WORD_TO_MEANING_READING -> correctWord.word
            WordQuizType.MEANING_READING_TO_WORD -> "${correctWord.meaning} / ${correctWord.reading}"
        }
        val options = when (quizType) {
            WordQuizType.WORD_TO_MEANING_READING -> shuffledOptions.map { "${it.meaning} / ${it.reading}" }
            WordQuizType.MEANING_READING_TO_WORD -> shuffledOptions.map { it.word }
        }

        // 7. WordQuiz 객체 생성 및 반환
        return WordQuiz(
            question = question,
            answer = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> "${correctWord.meaning} / ${correctWord.reading}"
                WordQuizType.MEANING_READING_TO_WORD -> correctWord.word
            },
            options = options,
            correctIndex = options.indexOf(
                when (quizType) {
                    WordQuizType.WORD_TO_MEANING_READING -> "${correctWord.meaning} / ${correctWord.reading}"
                    WordQuizType.MEANING_READING_TO_WORD -> correctWord.word
                }
            )
        )
    }

    // 학습 모드용 데이터 로드
    suspend fun loadLearningModeWords(level: Level): Pair<List<Word>, List<Word>> {
        return repository.getWordsForLearningMode(level.toString())
    }

    // 메모리에 있는 데이터로 퀴즈 생성
    fun generateQuizFromMemory(
        correctWord: Word,
        distractors: List<Word>,
        quizType: WordQuizType
    ): WordQuiz {
        // 오답 3개 선택 (매번 다르게)
        val wrongOptions = distractors.shuffled().take(3)
        
        // 4개의 보기를 만들고 섞기
        val allOptions = (wrongOptions + correctWord).shuffled()
        
        return generateQuiz(correctWord, wrongOptions, quizType)
    }

    private fun generateQuiz(correctWord: Word, wrongWords: List<Word>, quizType: WordQuizType): WordQuiz {
        val allOptions = (wrongWords + correctWord).shuffled()
        
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
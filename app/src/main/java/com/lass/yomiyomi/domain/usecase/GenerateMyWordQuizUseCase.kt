package com.lass.yomiyomi.domain.usecase

import androidx.lifecycle.map
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.data.repository.MyWordRepository
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.data.WordQuiz
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import javax.inject.Inject

class GenerateMyWordQuizUseCase @Inject constructor(
    private val myWordRepository: MyWordRepository,
    private val wordRepository: WordRepository
) {
    
    suspend fun generateQuiz(level: Level, quizType: WordQuizType, isLearningMode: Boolean = false): WordQuiz? {
        return if (isLearningMode) {
            generateLearningModeQuiz(level, quizType)
        } else {
            generateRandomModeQuiz(level, quizType)
        }
    }
    
    // 학습 모드 퀴즈 생성 (가중치 기반)
    private suspend fun generateLearningModeQuiz(level: Level, quizType: WordQuizType): WordQuiz? {
        val (priorityWords, distractors) = myWordRepository.getMyWordsForLearningMode(level.value ?: "ALL")
        
        if (priorityWords.isEmpty()) {
            // 선택한 레벨에 우선순위 데이터가 없으면 랜덤 모드로 폴백
            return generateRandomModeQuiz(level, quizType)
        }
        
        val correctWord = priorityWords.random()
        
        // 오답 선택지 생성 (다른 레벨이나 원본 데이터 사용 가능)
        return createQuizWithCorrectAnswer(correctWord, quizType)
    }
    
    // 랜덤 모드 퀴즈 생성 (수정된 로직)
    private suspend fun generateRandomModeQuiz(level: Level, quizType: WordQuizType): WordQuiz? {
        // 정답은 반드시 선택한 레벨에서만 선택
        val levelWords = getMyWordsByLevel(level)
        if (levelWords.isEmpty()) {
            // 선택한 레벨에 데이터가 없으면 null 반환 (데이터 부족 표시)
            return null
        }
        
        // 정답 선택 (선택한 레벨에서만)
        val correctWord = levelWords.random()
        
        // 오답 선택지 생성 (다른 레벨이나 원본 데이터 사용 가능)
        return createQuizWithCorrectAnswer(correctWord, quizType)
    }
    
    private suspend fun getMyWordsByLevel(level: Level): List<MyWordItem> {
        return if (level == Level.ALL) {
            myWordRepository.getAllMyWords()
        } else {
            myWordRepository.getAllMyWordsByLevel(level.value ?: "")
        }
    }
    
    private suspend fun getMyWordsWithAdjacentLevels(level: Level): List<MyWordItem> {
        val adjacentLevels = getAdjacentLevels(level)
        val allWords = mutableListOf<MyWordItem>()
        
        for (lvl in adjacentLevels) {
            allWords.addAll(getMyWordsByLevel(lvl))
        }
        
        return allWords.distinctBy { it.id }
    }
    
    private fun getAdjacentLevels(level: Level): List<Level> {
        return when (level) {
            Level.N5 -> listOf(Level.N5, Level.N4)
            Level.N4 -> listOf(Level.N5, Level.N4, Level.N3)
            Level.N3 -> listOf(Level.N4, Level.N3, Level.N2)
            Level.N2 -> listOf(Level.N3, Level.N2, Level.N1)
            Level.N1 -> listOf(Level.N2, Level.N1)
            Level.ALL -> listOf(Level.ALL)
        }
    }
    
    private fun createQuizFromMyWords(myWords: List<MyWordItem>, quizType: WordQuizType): WordQuiz {
        val correctWord = myWords.random()
        val wrongOptions = myWords.filter { it.id != correctWord.id }.shuffled().take(3)
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
    
    private suspend fun createHybridQuiz(correctMyWord: MyWordItem, quizType: WordQuizType): WordQuiz {
        // 정답은 MyWord에서, 오답은 원본 Word에서 가져오기
        val originalWords = wordRepository.getAllWords()
        val wrongOptionsAsMyWords = originalWords.shuffled().take(3).map { wordItem ->
            // WordItem을 MyWordItem으로 변환
            MyWordItem(
                id = wordItem.id,
                word = wordItem.word,
                reading = wordItem.reading,
                type = wordItem.type,
                meaning = wordItem.meaning,
                level = wordItem.level,
                learningWeight = wordItem.learningWeight,
                timestamp = wordItem.timestamp
            )
        }
        
        val allOptions = (wrongOptionsAsMyWords + correctMyWord).shuffled()
        
        return WordQuiz(
            question = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> correctMyWord.word
                WordQuizType.MEANING_READING_TO_WORD -> "${correctMyWord.meaning} / ${correctMyWord.reading}"
            },
            answer = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> "${correctMyWord.meaning} / ${correctMyWord.reading}"
                WordQuizType.MEANING_READING_TO_WORD -> correctMyWord.word
            },
            options = when (quizType) {
                WordQuizType.WORD_TO_MEANING_READING -> allOptions.map { "${it.meaning} / ${it.reading}" }
                WordQuizType.MEANING_READING_TO_WORD -> allOptions.map { it.word }
            },
            correctIndex = allOptions.indexOf(correctMyWord)
        )
    }
    
    private suspend fun createQuizWithCorrectAnswer(correctWord: MyWordItem, quizType: WordQuizType): WordQuiz {
        // 오답 선택지 생성 - 우선순위: 다른 MyWord → 원본 Word
        val wrongOptions = mutableListOf<MyWordItem>()
        
        // 1. 다른 MyWord에서 오답 선택지 찾기
        val allMyWords = myWordRepository.getAllMyWords().filter { it.id != correctWord.id }
        wrongOptions.addAll(allMyWords.shuffled().take(3))
        
        // 2. MyWord가 부족하면 원본 Word에서 보충
        if (wrongOptions.size < 3) {
            val originalWords = wordRepository.getAllWords()
            val usedIds = (wrongOptions + correctWord).map { it.id }.toSet()
            val additionalOptions = originalWords
                .filter { it.id !in usedIds }
                .shuffled()
                .take(3 - wrongOptions.size)
                .map { wordItem ->
                    // WordItem을 MyWordItem으로 변환
                    MyWordItem(
                        id = wordItem.id,
                        word = wordItem.word,
                        reading = wordItem.reading,
                        type = wordItem.type,
                        meaning = wordItem.meaning,
                        level = wordItem.level,
                        learningWeight = wordItem.learningWeight,
                        timestamp = wordItem.timestamp
                    )
                }
            wrongOptions.addAll(additionalOptions)
        }
        
        // 3개의 오답 선택지만 사용 (부족하면 부족한 대로)
        val finalWrongOptions = wrongOptions.take(3)
        val allOptions = (finalWrongOptions + correctWord).shuffled()
        
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

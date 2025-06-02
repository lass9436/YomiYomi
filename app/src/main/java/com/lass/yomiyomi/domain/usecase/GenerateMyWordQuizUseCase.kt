package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.MyWordItem
import com.lass.yomiyomi.domain.model.WordItem
import com.lass.yomiyomi.data.repository.MyWordRepository
import com.lass.yomiyomi.data.repository.WordRepository
import com.lass.yomiyomi.domain.model.WordQuiz
import com.lass.yomiyomi.domain.model.WordQuizType
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
            // 학습 모드용 데이터가 없으면 랜덤 모드로 폴백
            return generateRandomModeQuiz(level, quizType)
        }
        
        val correctWord = priorityWords.random()
        val allMyWords = priorityWords + distractors
        val wrongOptions = allMyWords.filter { it.id != correctWord.id }.shuffled().take(3)
        
        if (wrongOptions.size < 3) {
            // 옵션이 부족하면 랜덤 모드로 폴백
            return generateRandomModeQuiz(level, quizType)
        }
        
        return createQuizFromMyWords(listOf(correctWord) + wrongOptions, quizType)
    }
    
    // 랜덤 모드 퀴즈 생성 (기존 로직)
    private suspend fun generateRandomModeQuiz(level: Level, quizType: WordQuizType): WordQuiz? {
        // 1단계: 같은 레벨에 4개 이상?
        val levelWords = getMyWordsByLevel(level)
        if (levelWords.size >= 4) {
            return createQuizFromMyWords(levelWords, quizType)
        }
        
        // 2단계: 인접 레벨 포함해서 4개 이상?
        val expandedWords = getMyWordsWithAdjacentLevels(level)
        if (expandedWords.size >= 4) {
            return createQuizFromMyWords(expandedWords, quizType)
        }
        
        // 3단계: 전체 MyWord로 4개 이상?
        val allMyWords = myWordRepository.getAllMyWords()
        if (allMyWords.size >= 4) {
            return createQuizFromMyWords(allMyWords, quizType)
        }
        
        // 4단계: 원본 데이터로 오답 보완
        if (allMyWords.isNotEmpty()) {
            return createHybridQuiz(allMyWords.random(), quizType)
        }
        
        // 5단계: 데이터 부족
        return null
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
} 

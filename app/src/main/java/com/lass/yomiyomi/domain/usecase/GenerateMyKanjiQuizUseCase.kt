package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.MyKanjiItem
import com.lass.yomiyomi.domain.model.KanjiItem
import com.lass.yomiyomi.data.repository.MyKanjiRepository
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.model.KanjiQuizType
import javax.inject.Inject

class GenerateMyKanjiQuizUseCase @Inject constructor(
    private val myKanjiRepository: MyKanjiRepository,
    private val kanjiRepository: KanjiRepository
) {
    
    suspend fun generateQuiz(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean = false): KanjiQuiz? {
        return if (isLearningMode) {
            generateLearningModeQuiz(level, quizType)
        } else {
            generateRandomModeQuiz(level, quizType)
        }
    }
    
    // 학습 모드 퀴즈 생성 (가중치 기반)
    private suspend fun generateLearningModeQuiz(level: Level, quizType: KanjiQuizType): KanjiQuiz? {
        val (priorityKanji, distractors) = myKanjiRepository.getMyKanjiForLearningMode(level.value ?: "ALL")
        
        if (priorityKanji.isEmpty()) {
            // 학습 모드용 데이터가 없으면 랜덤 모드로 폴백
            return generateRandomModeQuiz(level, quizType)
        }
        
        val correctKanji = priorityKanji.random()
        val allMyKanji = priorityKanji + distractors
        val wrongOptions = allMyKanji.filter { it.id != correctKanji.id }.shuffled().take(3)
        
        if (wrongOptions.size < 3) {
            // 옵션이 부족하면 랜덤 모드로 폴백
            return generateRandomModeQuiz(level, quizType)
        }
        
        return createQuizFromMyKanji(listOf(correctKanji) + wrongOptions, quizType)
    }
    
    // 랜덤 모드 퀴즈 생성 (기존 로직)
    private suspend fun generateRandomModeQuiz(level: Level, quizType: KanjiQuizType): KanjiQuiz? {
        // 1단계: 같은 레벨에 4개 이상?
        val levelKanji = getMyKanjiByLevel(level)
        if (levelKanji.size >= 4) {
            return createQuizFromMyKanji(levelKanji, quizType)
        }
        
        // 2단계: 인접 레벨 포함해서 4개 이상?
        val expandedKanji = getMyKanjiWithAdjacentLevels(level)
        if (expandedKanji.size >= 4) {
            return createQuizFromMyKanji(expandedKanji, quizType)
        }
        
        // 3단계: 전체 MyKanji로 4개 이상?
        val allMyKanji = myKanjiRepository.getAllMyKanji()
        if (allMyKanji.size >= 4) {
            return createQuizFromMyKanji(allMyKanji, quizType)
        }
        
        // 4단계: 원본 데이터로 오답 보완
        if (allMyKanji.isNotEmpty()) {
            return createHybridQuiz(allMyKanji.random(), quizType)
        }
        
        // 5단계: 데이터 부족
        return null
    }
    
    private suspend fun getMyKanjiByLevel(level: Level): List<MyKanjiItem> {
        return if (level == Level.ALL) {
            myKanjiRepository.getAllMyKanji()
        } else {
            myKanjiRepository.getAllMyKanjiByLevel(level.value ?: "")
        }
    }
    
    private suspend fun getMyKanjiWithAdjacentLevels(level: Level): List<MyKanjiItem> {
        val adjacentLevels = getAdjacentLevels(level)
        val allKanji = mutableListOf<MyKanjiItem>()
        
        for (lvl in adjacentLevels) {
            allKanji.addAll(getMyKanjiByLevel(lvl))
        }
        
        return allKanji.distinctBy { it.id }
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
    
    private fun createQuizFromMyKanji(myKanji: List<MyKanjiItem>, quizType: KanjiQuizType): KanjiQuiz {
        val correctKanji = myKanji.random()
        val wrongOptions = myKanji.filter { it.id != correctKanji.id }.shuffled().take(3)
        val allOptions = (wrongOptions + correctKanji).shuffled()
        
        return KanjiQuiz(
            question = when (quizType) {
                KanjiQuizType.KANJI_TO_READING_MEANING -> correctKanji.kanji
                KanjiQuizType.READING_MEANING_TO_KANJI -> "${correctKanji.kunyomi} / ${correctKanji.meaning}"
            },
            answer = when (quizType) {
                KanjiQuizType.KANJI_TO_READING_MEANING -> "${correctKanji.kunyomi} / ${correctKanji.meaning}"
                KanjiQuizType.READING_MEANING_TO_KANJI -> correctKanji.kanji
            },
            options = when (quizType) {
                KanjiQuizType.KANJI_TO_READING_MEANING -> allOptions.map { "${it.kunyomi} / ${it.meaning}" }
                KanjiQuizType.READING_MEANING_TO_KANJI -> allOptions.map { it.kanji }
            },
            correctIndex = allOptions.indexOf(correctKanji)
        )
    }
    
    private suspend fun createHybridQuiz(correctMyKanji: MyKanjiItem, quizType: KanjiQuizType): KanjiQuiz {
        // 정답은 MyKanji에서, 오답은 원본 Kanji에서 가져오기
        val originalKanji = kanjiRepository.getAllKanji()
        val wrongOptionsAsMyKanji = originalKanji.shuffled().take(3).map { kanjiItem ->
            // KanjiItem을 MyKanjiItem으로 변환
            MyKanjiItem(
                id = kanjiItem.id,
                kanji = kanjiItem.kanji,
                onyomi = kanjiItem.onyomi,
                kunyomi = kanjiItem.kunyomi,
                meaning = kanjiItem.meaning,
                level = kanjiItem.level,
                learningWeight = kanjiItem.learningWeight,
                timestamp = kanjiItem.timestamp
            )
        }
        
        val allOptions = (wrongOptionsAsMyKanji + correctMyKanji).shuffled()
        
        return KanjiQuiz(
            question = when (quizType) {
                KanjiQuizType.KANJI_TO_READING_MEANING -> correctMyKanji.kanji
                KanjiQuizType.READING_MEANING_TO_KANJI -> "${correctMyKanji.kunyomi} / ${correctMyKanji.meaning}"
            },
            answer = when (quizType) {
                KanjiQuizType.KANJI_TO_READING_MEANING -> "${correctMyKanji.kunyomi} / ${correctMyKanji.meaning}"
                KanjiQuizType.READING_MEANING_TO_KANJI -> correctMyKanji.kanji
            },
            options = when (quizType) {
                KanjiQuizType.KANJI_TO_READING_MEANING -> allOptions.map { "${it.kunyomi} / ${it.meaning}" }
                KanjiQuizType.READING_MEANING_TO_KANJI -> allOptions.map { it.kanji }
            },
            correctIndex = allOptions.indexOf(correctMyKanji)
        )
    }
} 

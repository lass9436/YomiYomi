package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiQuiz

class GenerateKanjiQuizByLevelUseCase(
    private val repository: KanjiRepository
) {
    suspend operator fun invoke(level: Level, isLearningMode: Boolean = false): KanjiQuiz {
        if (isLearningMode) {
            throw IllegalStateException("Learning mode should use loadLearningModeWords and generateQuizFromMemory")
        }

        // 랜덤 모드: 기존 로직
        val kanjiList = repository.getAllKanjiByLevel(level.toString())
        val correctKanji = kanjiList.random()
        val shuffledOptions = kanjiList
            .filter { it != correctKanji }
            .shuffled()
            .take(3)
            .toMutableList()

        if (!shuffledOptions.contains(correctKanji)) {
            shuffledOptions.add(correctKanji)
        }
        shuffledOptions.shuffle()

        return KanjiQuiz(
            kanji = correctKanji.kanji,
            correctString = select(correctKanji),
            optionStrings = shuffledOptions.map { select(it) },
            correctIndex = shuffledOptions.indexOf(correctKanji)
        )
    }

    // 학습 모드용 데이터 로드
    suspend fun loadLearningModeWords(level: Level): Pair<List<Kanji>, List<Kanji>> {
        return repository.getKanjiForLearningMode(level.toString())
    }

    // 메모리에 있는 데이터로 퀴즈 생성
    fun generateQuizFromMemory(
        correctKanji: Kanji,
        distractors: List<Kanji>,
        isLearningMode: Boolean
    ): KanjiQuiz {
        // 오답 3개 선택 (매번 다르게)
        val wrongOptions = distractors.shuffled().take(3)
        
        // 4개의 보기를 만들고 섞기
        val allOptions = (wrongOptions + correctKanji).shuffled()
        
        return KanjiQuiz(
            kanji = correctKanji.kanji,
            correctString = select(correctKanji),
            optionStrings = allOptions.map { select(it) },
            correctIndex = allOptions.indexOf(correctKanji)
        )
    }

    private fun select(kanji: Kanji): String {
        return "${kanji.kunyomi} / ${kanji.meaning}"
    }
}
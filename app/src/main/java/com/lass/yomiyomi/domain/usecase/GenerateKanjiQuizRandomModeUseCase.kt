package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.model.KanjiQuizType
import javax.inject.Inject

class GenerateKanjiQuizRandomModeUseCase @Inject constructor(
    private val repository: KanjiRepository
) {
    suspend operator fun invoke(level: Level, quizType: KanjiQuizType): KanjiQuiz {
        val kanjiList = repository.getAllKanjiByLevel(level.toString())
        val correctKanji = kanjiList.random()
        val shuffledOptions = kanjiList
            .filter { it != correctKanji }
            .shuffled()
            .take(3)
            .toMutableList()
            .apply { add(correctKanji); shuffle() }

        return generateQuiz(correctKanji, shuffledOptions, quizType)
    }

    private fun generateQuiz(correctKanji: Kanji, allOptions: List<Kanji>, quizType: KanjiQuizType): KanjiQuiz {
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
} 
package com.lass.yomiyomi.domain.usecase

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.repository.KanjiRepository
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.model.KanjiQuizType
import javax.inject.Inject

class GenerateKanjiQuizStudyModeUseCase @Inject constructor(
    private val repository: KanjiRepository
) {
    // 학습 모드용 데이터 로드
    suspend fun loadLearningModeData(level: Level): Pair<List<Kanji>, List<Kanji>> {
        return repository.getKanjiForLearningMode(level.toString())
    }

    // 퀴즈 생성
    fun generateQuiz(correctKanji: Kanji, distractors: List<Kanji>, quizType: KanjiQuizType): KanjiQuiz {
        // 오답 3개 선택 (매번 다르게)
        val wrongOptions = distractors.shuffled().take(3)
        
        // 4개의 보기를 만들고 섞기
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
} 
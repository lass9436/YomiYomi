package com.lass.yomiyomi.viewmodel.kanji.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.KanjiQuiz
import com.lass.yomiyomi.domain.model.constant.KanjiQuizType
import kotlinx.coroutines.flow.StateFlow

interface KanjiQuizViewModelInterface {
    val quizState: StateFlow<KanjiQuiz?> // 퀴즈 데이터 상태
    val isLoading: StateFlow<Boolean> // 로딩 상태

    fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean)
    fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean)
}

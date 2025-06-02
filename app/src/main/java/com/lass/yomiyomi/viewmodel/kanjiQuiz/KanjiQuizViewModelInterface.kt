package com.lass.yomiyomi.viewmodel.kanjiQuiz

import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.model.KanjiQuizType
import kotlinx.coroutines.flow.StateFlow

interface KanjiQuizViewModelInterface {
    val quizState: StateFlow<KanjiQuiz?> // 퀴즈 데이터 상태
    val isLoading: StateFlow<Boolean> // 로딩 상태

    fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean)
    fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean)
}

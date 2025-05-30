package com.lass.yomiyomi.viewmodel.wordQuiz

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.domain.model.WordQuiz
import com.lass.yomiyomi.domain.model.WordQuizType
import kotlinx.coroutines.flow.StateFlow

interface WordQuizViewModelInterface {
    val quizState: StateFlow<WordQuiz?> // 퀴즈 데이터 상태
    val isLoading: StateFlow<Boolean> // 로딩 상태

    fun loadQuizByLevel(level: Level, quizType: WordQuizType, isLearningMode: Boolean = false)
    fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean)
}
package com.lass.yomiyomi.viewmodel.word.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.WordQuiz
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import kotlinx.coroutines.flow.StateFlow

interface WordQuizViewModelInterface {
    val quizState: StateFlow<WordQuiz?> // 퀴즈 데이터 상태
    val isLoading: StateFlow<Boolean> // 로딩 상태

    fun loadQuizByLevel(level: Level, quizType: WordQuizType, isLearningMode: Boolean = false)
    fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean)
}

package com.lass.yomiyomi.viewmodel.myWordQuiz

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.domain.model.WordQuiz
import com.lass.yomiyomi.domain.model.WordQuizType
import kotlinx.coroutines.flow.StateFlow

interface MyWordQuizViewModelInterface {
    val quizState: StateFlow<WordQuiz?>
    val isLoading: StateFlow<Boolean>
    val hasInsufficientData: StateFlow<Boolean>
    
    fun loadQuizByLevel(level: Level, quizType: WordQuizType, isLearningMode: Boolean = false)
    fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean = false)
} 
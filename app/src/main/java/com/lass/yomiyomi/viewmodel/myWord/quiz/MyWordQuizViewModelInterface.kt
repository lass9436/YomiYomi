package com.lass.yomiyomi.viewmodel.myWord.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.WordQuiz
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import kotlinx.coroutines.flow.StateFlow

interface MyWordQuizViewModelInterface {
    val quizState: StateFlow<WordQuiz?>
    val isLoading: StateFlow<Boolean>
    val hasInsufficientData: StateFlow<Boolean>
    
    fun loadQuizByLevel(level: Level, quizType: WordQuizType, isLearningMode: Boolean = false)
    fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean = false)
} 

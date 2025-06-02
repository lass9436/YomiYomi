package com.lass.yomiyomi.viewmodel.myKanjiQuiz

import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.model.KanjiQuizType
import kotlinx.coroutines.flow.StateFlow

interface MyKanjiQuizViewModelInterface {
    val quizState: StateFlow<KanjiQuiz?>
    val isLoading: StateFlow<Boolean>
    val hasInsufficientData: StateFlow<Boolean>
    
    fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean = false)
    fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean = false)
} 

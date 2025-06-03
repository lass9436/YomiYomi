package com.lass.yomiyomi.viewmodel.myKanjiQuiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.KanjiQuiz
import com.lass.yomiyomi.domain.model.constant.KanjiQuizType
import kotlinx.coroutines.flow.StateFlow

interface MyKanjiQuizViewModelInterface {
    val quizState: StateFlow<KanjiQuiz?>
    val isLoading: StateFlow<Boolean>
    val hasInsufficientData: StateFlow<Boolean>
    
    fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean = false)
    fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean = false)
} 

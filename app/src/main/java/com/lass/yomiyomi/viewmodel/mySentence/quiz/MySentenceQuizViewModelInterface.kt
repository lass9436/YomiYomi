package com.lass.yomiyomi.viewmodel.mySentence.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.SentenceQuizState
import kotlinx.coroutines.flow.StateFlow

interface MySentenceQuizViewModelInterface {
    val isLoading: StateFlow<Boolean>
    val selectedLevel: StateFlow<Level>
    val quizState: StateFlow<SentenceQuizState>
    val availableLevels: StateFlow<List<Level>>
    
    fun setSelectedLevel(level: Level)
    fun startQuiz()
    fun showAnswer()
    fun answerCorrect()
    fun answerIncorrect()
    fun nextQuestion()
    fun resetQuiz()
} 
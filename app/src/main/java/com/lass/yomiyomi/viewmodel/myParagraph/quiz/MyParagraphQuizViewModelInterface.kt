package com.lass.yomiyomi.viewmodel.myParagraph.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.ParagraphQuizState
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.StateFlow

interface MyParagraphQuizViewModelInterface {
    val isLoading: StateFlow<Boolean>
    val selectedLevel: StateFlow<Level>
    val quizState: StateFlow<ParagraphQuizState>
    val availableLevels: StateFlow<List<Level>>
    
    fun setSelectedLevel(level: Level)
    fun startQuiz()
    fun showAnswer()
    fun answerCorrect()
    fun answerIncorrect()
    fun nextQuestion()
    fun resetQuiz()
    fun getCurrentSentence(): SentenceItem?
} 
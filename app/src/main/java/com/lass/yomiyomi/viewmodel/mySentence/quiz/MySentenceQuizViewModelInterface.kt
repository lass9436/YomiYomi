package com.lass.yomiyomi.viewmodel.mySentence.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.SentenceQuiz
import com.lass.yomiyomi.domain.model.constant.SentenceQuizType
import kotlinx.coroutines.flow.StateFlow

interface MySentenceQuizViewModelInterface {
    val quizState: StateFlow<SentenceQuiz?>
    val isLoading: StateFlow<Boolean>
    val hasInsufficientData: StateFlow<Boolean>
    val isListening: StateFlow<Boolean>
    val recognizedText: StateFlow<String>
    
    fun loadQuizByLevel(level: Level, quizType: SentenceQuizType, isLearningMode: Boolean = false)
    fun startListening()
    fun stopListening()
    fun checkAnswer(recognizedAnswer: String): Boolean
    fun clearRecognizedText()
} 
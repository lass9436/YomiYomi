package com.lass.yomiyomi.viewmodel.mySentence.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.SentenceQuiz
import com.lass.yomiyomi.domain.model.constant.SentenceQuizType
import kotlinx.coroutines.flow.StateFlow

interface MySentenceQuizViewModelInterface {
    val quizState: StateFlow<SentenceQuiz?>
    val isLoading: StateFlow<Boolean>
    val hasInsufficientData: StateFlow<Boolean>
    val isListening: StateFlow<Boolean> // SpeechRecognitionManager 기준
    val recognizedText: StateFlow<String> // SpeechRecognitionManager 기준
    
    fun loadQuizByLevel(level: Level, quizType: SentenceQuizType, isLearningMode: Boolean = false)
    fun loadQuizBySentenceId(sentenceId: Int, quizType: SentenceQuizType)
    fun changeQuizType(quizType: SentenceQuizType)
    fun startListening() // SpeechRecognitionManager 기준
    fun stopListening() // SpeechRecognitionManager 기준
    fun checkAnswer(recognizedAnswer: String): Boolean
    fun clearRecognizedText() // SpeechRecognitionManager 기준
} 
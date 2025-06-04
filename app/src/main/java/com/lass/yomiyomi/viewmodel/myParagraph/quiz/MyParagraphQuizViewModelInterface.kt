package com.lass.yomiyomi.viewmodel.myParagraph.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.domain.model.constant.ParagraphQuizType
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.StateFlow

interface MyParagraphQuizViewModelInterface {
    val quizState: StateFlow<ParagraphQuiz?>
    val isLoading: StateFlow<Boolean>
    val hasInsufficientData: StateFlow<Boolean>
    val isListening: StateFlow<Boolean>
    val recognizedText: StateFlow<String>
    val isQuizCompleted: StateFlow<Boolean>
    val sentences: StateFlow<List<SentenceItem>>
    
    fun loadQuizByLevel(level: Level, quizType: ParagraphQuizType)
    fun startListening()
    fun stopListening()
    fun processRecognizedText(recognizedText: String): List<String> // 새로 채워진 빈칸들 반환
    fun clearRecognizedText()
    fun resetQuiz() // 빈칸들을 모두 비우고 다시 시작
} 
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
    val currentSentence: StateFlow<SentenceItem?> // 단일 문장 퀴즈용
    
    fun loadQuizByLevel(level: Level, quizType: ParagraphQuizType)
    fun loadQuizBySentence(sentence: SentenceItem, quizType: ParagraphQuizType) // 단일 문장 퀴즈 로드
    fun loadQuizBySentenceId(sentenceId: Int, quizType: ParagraphQuizType) // 문장 ID로 단일 문장 퀴즈 로드
    fun startListening()
    fun stopListening()
    /**
     * 음성 인식된 텍스트를 처리하여 빈칸을 채움
     * @param recognizedAnswer 음성 인식된 답
     * @return 새로 채워진 정답들의 리스트
     */
    fun processRecognizedText(recognizedAnswer: String): List<String>
    fun clearRecognizedText()
    fun resetQuiz() // 빈칸들을 모두 비우고 다시 시작
    fun showAllAnswers() // 모든 빈칸을 정답으로 채우기
} 
package com.lass.yomiyomi.viewmodel.myParagraph.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.ParagraphQuizState
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyMyParagraphQuizViewModel : MyParagraphQuizViewModelInterface {
    
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _selectedLevel = MutableStateFlow(Level.ALL)
    override val selectedLevel: StateFlow<Level> = _selectedLevel
    
    private val _quizState = MutableStateFlow(ParagraphQuizState())
    override val quizState: StateFlow<ParagraphQuizState> = _quizState
    
    private val _availableLevels = MutableStateFlow(Level.values().toList())
    override val availableLevels: StateFlow<List<Level>> = _availableLevels
    
    private val dummyParagraph = ParagraphItem(
        paragraphId = "dummy_1",
        title = "더미 문단",
        description = "프리뷰용 더미 문단입니다",
        category = "일반",
        level = Level.N5,
        totalSentences = 1,
        actualSentenceCount = 1,
        createdAt = System.currentTimeMillis()
    )
    
    private val dummySentence = SentenceItem(
        id = 1,
        japanese = "これは日本語です。",
        korean = "이것은 일본어입니다.",
        paragraphId = "dummy_1",
        orderInParagraph = 1,
        category = "일반",
        level = Level.N5,
        learningProgress = 0.0f,
        reviewCount = 0,
        lastReviewedAt = null,
        createdAt = System.currentTimeMillis()
    )
    
    override fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }
    
    override fun startQuiz() {
        _quizState.value = ParagraphQuizState(
            currentParagraph = dummyParagraph,
            currentSentences = listOf(dummySentence),
            currentSentenceIndex = 0,
            currentParagraphIndex = 0,
            totalParagraphs = 1,
            score = 0,
            isAnswered = false,
            isQuizFinished = false,
            showAnswer = false
        )
    }
    
    override fun showAnswer() {
        val currentState = _quizState.value
        _quizState.value = currentState.copy(showAnswer = true)
    }
    
    override fun answerCorrect() {
        val currentState = _quizState.value
        _quizState.value = currentState.copy(
            score = currentState.score + 1,
            isAnswered = true
        )
    }
    
    override fun answerIncorrect() {
        val currentState = _quizState.value
        _quizState.value = currentState.copy(isAnswered = true)
    }
    
    override fun nextQuestion() {
        val currentState = _quizState.value
        _quizState.value = currentState.copy(isQuizFinished = true)
    }
    
    override fun resetQuiz() {
        _quizState.value = ParagraphQuizState()
    }
    
    override fun getCurrentSentence(): SentenceItem? {
        return dummySentence
    }
} 
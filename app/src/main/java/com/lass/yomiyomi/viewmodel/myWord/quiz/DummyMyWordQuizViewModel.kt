package com.lass.yomiyomi.viewmodel.myWord.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.WordQuiz
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyMyWordQuizViewModel : MyWordQuizViewModelInterface {

    // 상태 Flow
    private val _quizState = MutableStateFlow<WordQuiz?>(null)
    override val quizState: StateFlow<WordQuiz?> get() = _quizState

    // 로딩 상태 Flow
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> get() = _isLoading

    // 데이터 부족 상태 Flow
    private val _hasInsufficientData = MutableStateFlow(false)
    override val hasInsufficientData: StateFlow<Boolean> get() = _hasInsufficientData

    override fun loadQuizByLevel(level: Level, quizType: WordQuizType, isLearningMode: Boolean) {
        _isLoading.value = true

        val dummyQuiz = when (quizType) {
            WordQuizType.WORD_TO_MEANING_READING -> {
                val options = listOf("먹다 / たべる", "마시다 / のむ", "보다 / みる", "듣다 / きく")
                WordQuiz(
                    question = "食べる",
                    answer = "먹다 / たべる",
                    options = options,
                    correctIndex = 0
                )
            }
            WordQuizType.MEANING_READING_TO_WORD -> {
                val options = listOf("飲む", "見る", "食べる", "聞く")
                WordQuiz(
                    question = "먹다 / たべる",
                    answer = "食べる",
                    options = options,
                    correctIndex = 2
                )
            }
        }

        _quizState.value = dummyQuiz
        _isLoading.value = false
    }

    override fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean) {
        // 더미 구현에서는 아무 동작도 하지 않음
    }
} 

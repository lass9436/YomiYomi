package com.lass.yomiyomi.viewmodel.wordQuiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.WordQuiz
import com.lass.yomiyomi.domain.model.constant.WordQuizType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyWordQuizViewModel : WordQuizViewModelInterface {

    // 상태 Flow
    private val _quizState = MutableStateFlow<WordQuiz?>(null)
    override val quizState: StateFlow<WordQuiz?> get() = _quizState

    // 로딩 상태 Flow
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> get() = _isLoading

    override fun loadQuizByLevel(level: Level, quizType: WordQuizType, isLearningMode: Boolean) {
        _isLoading.value = true

        val dummyQuiz = when (quizType) {
            WordQuizType.WORD_TO_MEANING_READING -> {
                val options = listOf("의미 / よみ", "뜻 / はな", "사랑 / あい", "물 / みず")
                WordQuiz(
                    question = "愛",
                    answer = "사랑 / あい",
                    options = options,
                    correctIndex = 2
                )
            }
            WordQuizType.MEANING_READING_TO_WORD -> {
                val options = listOf("水", "花", "愛", "火")
                WordQuiz(
                    question = "사랑 / あい",
                    answer = "愛",
                    options = options,
                    correctIndex = 2
                )
            }
        }

        _quizState.value = dummyQuiz
        _isLoading.value = false
    }

    override fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean) {
        // Dummy implementation - do nothing
    }
}

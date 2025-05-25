package com.lass.yomiyomi.viewmodel.wordQuiz

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.domain.model.WordQuiz
import com.lass.yomiyomi.domain.model.WordQuizType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyWordQuizViewModel : WordQuizViewModelInterface {

    // 상태 Flow
    private val _quizState = MutableStateFlow<WordQuiz?>(null)
    override val quizState: StateFlow<WordQuiz?> get() = _quizState

    // 로딩 상태 Flow
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> get() = _isLoading

    override fun loadQuizByLevel(level: Level, quizType: WordQuizType) {
        // 더미 데이터를 즉시 반환
        _isLoading.value = true

        // 퀴즈 타입에 따라 더미 데이터 구성
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
}
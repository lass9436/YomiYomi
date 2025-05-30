package com.lass.yomiyomi.viewmodel.kanjiQuiz

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.domain.model.KanjiQuizType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyKanjiQuizViewModel : KanjiQuizViewModelInterface {

    // 상태 Flow
    private val _quizState = MutableStateFlow<KanjiQuiz?>(null)
    override val quizState: StateFlow<KanjiQuiz?> get() = _quizState

    // 로딩 상태 Flow
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> get() = _isLoading

    override fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean) {
        // 더미 데이터를 즉시 반환
        _isLoading.value = true

        // 퀴즈 데이터를 구성
        val question = if (quizType == KanjiQuizType.KANJI_TO_READING_MEANING) "火" else "ひ / 불"
        val answer = if (quizType == KanjiQuizType.KANJI_TO_READING_MEANING) "ひ / 불" else "火"
        val options = if (quizType == KanjiQuizType.KANJI_TO_READING_MEANING) {
            listOf("ひ / 불", "か / 화재", "ほのお / 화염", "ひかり / 빛")
        } else {
            listOf("火", "炎", "熱", "光")
        }
        val correctIndex = 0

        // KanjiQuiz 객체 생성
        _quizState.value = KanjiQuiz(
            question = question,
            answer = answer,
            options = options,
            correctIndex = correctIndex
        )

        _isLoading.value = false
    }

    override fun checkAnswer(selectedIndex: Int, isLearningMode: Boolean) {
        // 더미 구현에서는 아무 동작도 하지 않음
    }
}
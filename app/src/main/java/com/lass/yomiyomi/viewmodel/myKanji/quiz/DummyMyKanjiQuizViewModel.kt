package com.lass.yomiyomi.viewmodel.myKanji.quiz

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.data.KanjiQuiz
import com.lass.yomiyomi.domain.model.constant.KanjiQuizType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyMyKanjiQuizViewModel : MyKanjiQuizViewModelInterface {

    // 상태 Flow
    private val _quizState = MutableStateFlow<KanjiQuiz?>(null)
    override val quizState: StateFlow<KanjiQuiz?> get() = _quizState

    // 로딩 상태 Flow
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> get() = _isLoading

    // 데이터 부족 상태 Flow
    private val _hasInsufficientData = MutableStateFlow(false)
    override val hasInsufficientData: StateFlow<Boolean> get() = _hasInsufficientData

    override fun loadQuizByLevel(level: Level, quizType: KanjiQuizType, isLearningMode: Boolean) {
        // 더미 데이터를 즉시 반환
        _isLoading.value = true

        // 퀴즈 데이터를 구성
        val question = if (quizType == KanjiQuizType.KANJI_TO_READING_MEANING) "心" else "こころ / 마음"
        val answer = if (quizType == KanjiQuizType.KANJI_TO_READING_MEANING) "こころ / 마음" else "心"
        val options = if (quizType == KanjiQuizType.KANJI_TO_READING_MEANING) {
            listOf("こころ / 마음", "しん / 심장", "き / 기", "あい / 사랑")
        } else {
            listOf("心", "愛", "気", "思")
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

package com.lass.yomiyomi.viewmodel.kanjiQuiz

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.domain.model.KanjiQuiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyKanjiQuizViewModel : KanjiQuizViewModelInterface {

    // 상태 Flow
    private val _quizState = MutableStateFlow<KanjiQuiz?>(null)
    override val quizState: StateFlow<KanjiQuiz?> get() = _quizState

    // 로딩 상태 Flow
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> get() = _isLoading

    override fun loadQuiz(correctAttributeSelector: (kanji: Kanji) -> String) {
        // 더미 데이터를 즉시 반환
        _isLoading.value = true

        // 퀴즈 데이터를 구성
        val options = listOf("ひ", "か", "ほのお", "ひかり")
        val correctIndex = 0 // 첫 번째 옵션 "ひ"가 정답
        val dummyKanji = "火"

        // KanjiQuiz 객체 생성
        _quizState.value = KanjiQuiz(
            kanji = dummyKanji,
            correctString = options[correctIndex],
            optionStrings = options,
            correctIndex = correctIndex
        )

        _isLoading.value = false
    }

    override fun loadQuizByLevel(level: Level) {
        // 더미 데이터를 즉시 반환
        _isLoading.value = true

        // 퀴즈 데이터를 구성
        val options = listOf("ひ / 불", "か / 화재", "ほのお / 화염", "ひかり / 빛") // 수정
        val correctIndex = 0 // 첫 번째 옵션 "ひ / 불"이 정답
        val dummyKanji = "火" // Kanji 문자

        // KanjiQuiz 객체 생성
        _quizState.value = KanjiQuiz(
            kanji = dummyKanji,
            correctString = options[correctIndex],
            optionStrings = options,
            correctIndex = correctIndex
        )

        _isLoading.value = false
    }
}
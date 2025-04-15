package com.lass.yomiyomi.viewmodel.kanjiQuiz

import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.domain.model.KanjiQuiz
import kotlinx.coroutines.flow.StateFlow

interface KanjiQuizViewModelInterface {
    val quizState: StateFlow<KanjiQuiz?> // 퀴즈 데이터 상태
    val isLoading: StateFlow<Boolean> // 로딩 상태

    fun loadQuiz(correctAttributeSelector: (kanji: Kanji) -> String)
}
package com.lass.yomiyomi.viewmodel

import com.lass.yomiyomi.data.model.Kanji
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyKanjiViewModel : KanjiViewModelInterface {
    private val _randomKanji = MutableStateFlow(
        Kanji(
            kanji = "木",
            onyomi = "モク",
            kunyomi = "き",
            meaning = "나무",
            level = "2"
        )
    )
    override val randomKanji: StateFlow<Kanji?> = _randomKanji

    override fun fetchRandomKanji() {
        // 실제로는 아무 동작하지 않음
    }
}
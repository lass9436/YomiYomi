package com.lass.yomiyomi.viewmodel.kanji.Random

import com.lass.yomiyomi.domain.model.entity.KanjiItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyKanjiRandomViewModel : KanjiRandomViewModelInterface {
    private val _randomKanji = MutableStateFlow(
        KanjiItem(
            id = 1,
            kanji = "木",
            onyomi = "モク",
            kunyomi = "き",
            meaning = "나무",
            level = "2",
            learningWeight = 0.5f,
            timestamp = 0L
        )
    )
    override val randomKanji: StateFlow<KanjiItem?> = _randomKanji

    override fun fetchRandomKanji() {
        // 실제로는 아무 동작하지 않음
    }

    override fun fetchRandomKanjiByLevel(level: String?) {
        // 실제로는 아무 동작하지 않음
    }
}

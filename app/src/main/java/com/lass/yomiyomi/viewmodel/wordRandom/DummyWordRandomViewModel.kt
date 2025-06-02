package com.lass.yomiyomi.viewmodel.wordRandom

import com.lass.yomiyomi.domain.model.WordItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyWordRandomViewModel : WordRandomViewModelInterface {
    private val _randomWord = MutableStateFlow(
        WordItem(
            id = 1,
            word = "学校",
            reading = "がっこう",
            meaning = "학교",
            type = "명사",
            level = "N5",
            learningWeight = 0.5f,
            timestamp = 0L
        )
    )
    override val randomWord: StateFlow<WordItem?> = _randomWord

    override fun fetchRandomWord() {
        // 실제로는 아무 동작하지 않음
    }

    override fun fetchRandomWordByLevel(level: String?) {
        // 실제로는 아무 동작하지 않음
    }
}
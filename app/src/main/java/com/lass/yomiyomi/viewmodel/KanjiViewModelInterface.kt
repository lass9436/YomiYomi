package com.lass.yomiyomi.viewmodel

import com.lass.yomiyomi.data.model.Kanji
import kotlinx.coroutines.flow.StateFlow

interface KanjiViewModelInterface {
    val randomKanji: StateFlow<Kanji?>
    fun fetchRandomKanji()
}
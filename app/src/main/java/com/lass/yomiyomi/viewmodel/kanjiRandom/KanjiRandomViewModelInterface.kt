package com.lass.yomiyomi.viewmodel.kanjiRandom

import com.lass.yomiyomi.data.model.Kanji
import kotlinx.coroutines.flow.StateFlow

interface KanjiRandomViewModelInterface {
    val randomKanji: StateFlow<Kanji?>
    fun fetchRandomKanji()
    fun fetchRandomKanjiByLevel(level: String?)
}
package com.lass.yomiyomi.viewmodel.kanjiRandom

import com.lass.yomiyomi.domain.model.KanjiItem
import kotlinx.coroutines.flow.StateFlow

interface KanjiRandomViewModelInterface {
    val randomKanji: StateFlow<KanjiItem?>
    fun fetchRandomKanji()
    fun fetchRandomKanjiByLevel(level: String?)
}
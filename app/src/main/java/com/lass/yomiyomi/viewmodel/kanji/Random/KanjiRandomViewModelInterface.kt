package com.lass.yomiyomi.viewmodel.kanji.Random

import com.lass.yomiyomi.domain.model.entity.KanjiItem
import kotlinx.coroutines.flow.StateFlow

interface KanjiRandomViewModelInterface {
    val randomKanji: StateFlow<KanjiItem?>
    fun fetchRandomKanji()
    fun fetchRandomKanjiByLevel(level: String?)
}

package com.lass.yomiyomi.viewmodel.wordRandom

import com.lass.yomiyomi.domain.model.WordItem
import kotlinx.coroutines.flow.StateFlow

interface WordRandomViewModelInterface {
    val randomWord: StateFlow<WordItem?>
    fun fetchRandomWord()
    fun fetchRandomWordByLevel(level: String?)
}
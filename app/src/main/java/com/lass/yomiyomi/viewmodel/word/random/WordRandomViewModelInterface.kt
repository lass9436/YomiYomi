package com.lass.yomiyomi.viewmodel.word.random

import com.lass.yomiyomi.domain.model.entity.WordItem
import kotlinx.coroutines.flow.StateFlow

interface WordRandomViewModelInterface {
    val randomWord: StateFlow<WordItem?>
    fun fetchRandomWord()
    fun fetchRandomWordByLevel(level: String?)
}

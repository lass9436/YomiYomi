package com.lass.yomiyomi.viewmodel.wordRandom

import com.lass.yomiyomi.data.model.Word
import kotlinx.coroutines.flow.StateFlow

interface WordRandomViewModelInterface {
    val randomWord: StateFlow<Word?>
    fun fetchRandomWord()
    fun fetchRandomWordByLevel(level: String?)
}
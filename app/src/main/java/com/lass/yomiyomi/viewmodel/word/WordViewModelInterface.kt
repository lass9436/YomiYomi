package com.lass.yomiyomi.viewmodel.word

import com.lass.yomiyomi.domain.model.WordItem
import com.lass.yomiyomi.domain.model.Level
import kotlinx.coroutines.flow.StateFlow

interface WordViewModelInterface {
    val words: StateFlow<List<WordItem>>
    val isLoading: StateFlow<Boolean>
    val selectedLevel: StateFlow<Level>

    fun setSelectedLevel(level: Level)
    fun searchWords(query: String)
} 

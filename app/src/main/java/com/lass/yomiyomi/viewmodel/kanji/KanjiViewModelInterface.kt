package com.lass.yomiyomi.viewmodel.kanji

import com.lass.yomiyomi.domain.model.KanjiItem
import com.lass.yomiyomi.domain.model.Level
import kotlinx.coroutines.flow.StateFlow

interface KanjiViewModelInterface {
    val kanji: StateFlow<List<KanjiItem>>
    val isLoading: StateFlow<Boolean>
    val selectedLevel: StateFlow<Level>

    fun setSelectedLevel(level: Level)
    fun searchKanji(query: String)
} 

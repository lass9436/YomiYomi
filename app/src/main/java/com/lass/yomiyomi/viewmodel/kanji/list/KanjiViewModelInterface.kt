package com.lass.yomiyomi.viewmodel.kanji.list

import com.lass.yomiyomi.domain.model.entity.KanjiItem
import com.lass.yomiyomi.domain.model.constant.Level
import kotlinx.coroutines.flow.StateFlow

interface KanjiViewModelInterface {
    val kanji: StateFlow<List<KanjiItem>>
    val isLoading: StateFlow<Boolean>
    val selectedLevel: StateFlow<Level>

    fun setSelectedLevel(level: Level)
    fun searchKanji(query: String)
} 

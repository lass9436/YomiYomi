package com.lass.yomiyomi.viewmodel.myKanji

import com.lass.yomiyomi.domain.model.entity.KanjiItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.MyKanjiItem
import kotlinx.coroutines.flow.StateFlow

interface MyKanjiViewModelInterface {
    val myKanji: StateFlow<List<MyKanjiItem>>
    val isLoading: StateFlow<Boolean>
    val selectedLevel: StateFlow<Level>
    val searchResults: StateFlow<List<KanjiItem>>
    val isSearching: StateFlow<Boolean>

    fun setSelectedLevel(level: Level)
    fun searchMyKanji(query: String)
    fun searchOriginalKanji(query: String)
    fun addKanjiToMyKanji(kanji: KanjiItem)
    fun addMyKanjiDirectly(kanji: String, onyomi: String, kunyomi: String, meaning: String, level: Level)
    fun updateMyKanji(myKanji: MyKanjiItem)
    fun deleteMyKanji(myKanji: MyKanjiItem)
} 

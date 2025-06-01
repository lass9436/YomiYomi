package com.lass.yomiyomi.ui.state

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.MyKanji

data class MyKanjiState(
    val myKanji: List<MyKanji>,
    val isLoading: Boolean,
    val selectedLevel: Level,
    val searchQuery: String,
    val showMyKanjiSearch: Boolean,
    val showAddDialog: Boolean,
    val editingKanji: MyKanji?
)

data class MyKanjiCallbacks(
    val onNavigateBack: () -> Unit,
    val onLevelSelected: (Level) -> Unit,
    val onSearchQueryChanged: (String) -> Unit,
    val onToggleSearch: () -> Unit,
    val onShowAddDialog: () -> Unit,
    val onDismissAddDialog: () -> Unit,
    val onEditKanji: (MyKanji) -> Unit,
    val onDismissEditDialog: () -> Unit,
    val onDeleteKanji: (MyKanji) -> Unit
) 
package com.lass.yomiyomi.ui.state

import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.MyKanjiItem

data class KanjiState(
    val myKanji: List<MyKanjiItem>,
    val isLoading: Boolean,
    val selectedLevel: Level,
    val searchQuery: String,
    val showMyKanjiSearch: Boolean,
    val showAddDialog: Boolean,
    val editingKanji: MyKanjiItem?
)

data class KanjiCallbacks(
    val onNavigateBack: () -> Unit,
    val onLevelSelected: (Level) -> Unit,
    val onSearchQueryChanged: (String) -> Unit,
    val onToggleSearch: () -> Unit,
    val onShowAddDialog: () -> Unit,
    val onDismissAddDialog: () -> Unit,
    val onEditKanji: (MyKanjiItem) -> Unit,
    val onDismissEditDialog: () -> Unit,
    val onDeleteKanji: (MyKanjiItem) -> Unit
) 

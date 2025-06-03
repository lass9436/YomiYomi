package com.lass.yomiyomi.ui.state

import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.MyWordItem

data class WordState(
    val myWords: List<MyWordItem>,
    val isLoading: Boolean,
    val selectedLevel: Level,
    val searchQuery: String,
    val showMyWordSearch: Boolean,
    val showAddDialog: Boolean,
    val editingWord: MyWordItem?
)

data class WordCallbacks(
    val onNavigateBack: () -> Unit,
    val onLevelSelected: (Level) -> Unit,
    val onSearchQueryChanged: (String) -> Unit,
    val onToggleSearch: () -> Unit,
    val onShowAddDialog: () -> Unit,
    val onDismissAddDialog: () -> Unit,
    val onEditWord: (MyWordItem) -> Unit,
    val onDismissEditDialog: () -> Unit,
    val onDeleteWord: (MyWordItem) -> Unit
) 

package com.lass.yomiyomi.ui.state

import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.MyWord

data class MyWordState(
    val myWords: List<MyWord>,
    val isLoading: Boolean,
    val selectedLevel: Level,
    val searchQuery: String,
    val showMyWordSearch: Boolean,
    val showAddDialog: Boolean,
    val editingWord: MyWord?
)

data class MyWordCallbacks(
    val onNavigateBack: () -> Unit,
    val onLevelSelected: (Level) -> Unit,
    val onSearchQueryChanged: (String) -> Unit,
    val onToggleSearch: () -> Unit,
    val onShowAddDialog: () -> Unit,
    val onDismissAddDialog: () -> Unit,
    val onEditWord: (MyWord) -> Unit,
    val onDismissEditDialog: () -> Unit,
    val onDeleteWord: (MyWord) -> Unit
) 
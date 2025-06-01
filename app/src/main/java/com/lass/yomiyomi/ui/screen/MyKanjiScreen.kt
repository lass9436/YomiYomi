package com.lass.yomiyomi.ui.screen

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.data.model.MyKanji
import com.lass.yomiyomi.ui.layout.MyKanjiLayout
import com.lass.yomiyomi.ui.state.MyKanjiState
import com.lass.yomiyomi.ui.state.MyKanjiCallbacks
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.myKanji.DummyMyKanjiViewModel
import com.lass.yomiyomi.viewmodel.myKanji.MyKanjiViewModel
import com.lass.yomiyomi.viewmodel.myKanji.MyKanjiViewModelInterface

@Composable
fun MyKanjiScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    myKanjiViewModel: MyKanjiViewModelInterface = hiltViewModel<MyKanjiViewModel>()
) {
    val myKanji by myKanjiViewModel.myKanji.collectAsState()
    val isLoading by myKanjiViewModel.isLoading.collectAsState()
    val selectedLevel by myKanjiViewModel.selectedLevel.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showMyKanjiSearch by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingKanji by remember { mutableStateOf<MyKanji?>(null) }

    val state = MyKanjiState(
        myKanji = myKanji,
        isLoading = isLoading,
        selectedLevel = selectedLevel,
        searchQuery = searchQuery,
        showMyKanjiSearch = showMyKanjiSearch,
        showAddDialog = showAddDialog,
        editingKanji = editingKanji
    )

    val callbacks = MyKanjiCallbacks(
        onNavigateBack = onNavigateBack,
        onLevelSelected = { myKanjiViewModel.setSelectedLevel(it) },
        onSearchQueryChanged = { 
            searchQuery = it
            myKanjiViewModel.searchMyKanji(it)
        },
        onToggleSearch = { showMyKanjiSearch = !showMyKanjiSearch },
        onShowAddDialog = { showAddDialog = true },
        onDismissAddDialog = { showAddDialog = false },
        onEditKanji = { editingKanji = it },
        onDismissEditDialog = { editingKanji = null },
        onDeleteKanji = { myKanjiViewModel.deleteMyKanji(it) }
    )

    MyKanjiLayout(
        state = state,
        callbacks = callbacks,
        viewModel = myKanjiViewModel,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MyKanjiScreenPreview() {
    YomiYomiTheme {
        MyKanjiScreen(
            onNavigateBack = {},
            myKanjiViewModel = DummyMyKanjiViewModel()
        )
    }
} 
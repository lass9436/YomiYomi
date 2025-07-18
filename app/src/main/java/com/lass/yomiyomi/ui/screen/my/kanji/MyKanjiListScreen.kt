package com.lass.yomiyomi.ui.screen.my.kanji

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.entity.MyKanjiItem
import com.lass.yomiyomi.ui.layout.MyKanjiLayout
import com.lass.yomiyomi.ui.state.KanjiState
import com.lass.yomiyomi.ui.state.KanjiCallbacks
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.myKanji.list.DummyMyKanjiViewModel
import com.lass.yomiyomi.viewmodel.myKanji.list.MyKanjiViewModel
import com.lass.yomiyomi.viewmodel.myKanji.list.MyKanjiViewModelInterface

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
    var editingKanji by remember { mutableStateOf<MyKanjiItem?>(null) }

    // 안드로이드 시스템 뒤로가기 버튼도 onNavigateBack과 같은 동작
    BackHandler { onNavigateBack() }

    val state = KanjiState(
        myKanji = myKanji,
        isLoading = isLoading,
        selectedLevel = selectedLevel,
        searchQuery = searchQuery,
        showMyKanjiSearch = showMyKanjiSearch,
        showAddDialog = showAddDialog,
        editingKanji = editingKanji
    )

    val callbacks = KanjiCallbacks(
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

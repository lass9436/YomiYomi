package com.lass.yomiyomi.ui.screen.my.word

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.ui.layout.MyWordLayout
import com.lass.yomiyomi.ui.state.WordState
import com.lass.yomiyomi.ui.state.WordCallbacks
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.myWord.list.DummyMyWordViewModel
import com.lass.yomiyomi.viewmodel.myWord.list.MyWordViewModel
import com.lass.yomiyomi.viewmodel.myWord.list.MyWordViewModelInterface

@Composable
fun MyWordScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    myWordViewModel: MyWordViewModelInterface = hiltViewModel<MyWordViewModel>()
) {
    val myWords by myWordViewModel.myWords.collectAsState()
    val isLoading by myWordViewModel.isLoading.collectAsState()
    val selectedLevel by myWordViewModel.selectedLevel.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showMyWordSearch by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<MyWordItem?>(null) }

    // 안드로이드 시스템 뒤로가기 버튼도 onNavigateBack과 같은 동작
    BackHandler { onNavigateBack() }

    val state = WordState(
        myWords = myWords,
        isLoading = isLoading,
        selectedLevel = selectedLevel,
        searchQuery = searchQuery,
        showMyWordSearch = showMyWordSearch,
        showAddDialog = showAddDialog,
        editingWord = editingWord
    )

    val callbacks = WordCallbacks(
        onNavigateBack = onNavigateBack,
        onLevelSelected = { myWordViewModel.setSelectedLevel(it) },
        onSearchQueryChanged = { 
            searchQuery = it
            myWordViewModel.searchMyWords(it)
        },
        onToggleSearch = { showMyWordSearch = !showMyWordSearch },
        onShowAddDialog = { showAddDialog = true },
        onDismissAddDialog = { showAddDialog = false },
        onEditWord = { editingWord = it },
        onDismissEditDialog = { editingWord = null },
        onDeleteWord = { myWordViewModel.deleteMyWord(it) }
    )

    MyWordLayout(
        state = state,
        callbacks = callbacks,
        viewModel = myWordViewModel,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MyWordScreenPreview() {
    YomiYomiTheme {
        MyWordScreen(
            onNavigateBack = {},
            myWordViewModel = DummyMyWordViewModel()
        )
    }
} 

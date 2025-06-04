package com.lass.yomiyomi.ui.screen.basic.word

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.ui.layout.WordListLayout
import com.lass.yomiyomi.ui.state.WordState
import com.lass.yomiyomi.ui.state.WordCallbacks
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.word.list.DummyWordViewModel
import com.lass.yomiyomi.viewmodel.word.list.WordViewModel
import com.lass.yomiyomi.viewmodel.word.list.WordViewModelInterface

@Composable
fun WordListScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    wordListViewModel: WordViewModelInterface = hiltViewModel<WordViewModel>()
) {
    val words by wordListViewModel.words.collectAsState()
    val isLoading by wordListViewModel.isLoading.collectAsState()
    val selectedLevel by wordListViewModel.selectedLevel.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showWordSearch by remember { mutableStateOf(false) }

    // 안드로이드 시스템 뒤로가기 버튼도 onNavigateBack과 같은 동작
    BackHandler { onNavigateBack() }

    val state = WordState(
        myWords = words.map { wordItem ->
            // WordItem을 MyWordItem으로 변환 (호환성을 위해)
            MyWordItem(
                id = wordItem.id,
                word = wordItem.word,
                reading = wordItem.reading,
                type = wordItem.type,
                meaning = wordItem.meaning,
                level = wordItem.level,
                learningWeight = wordItem.learningWeight,
                timestamp = wordItem.timestamp
            )
        },
        isLoading = isLoading,
        selectedLevel = selectedLevel,
        searchQuery = searchQuery,
        showMyWordSearch = showWordSearch,
        showAddDialog = false, // 읽기 전용이므로 추가 기능 없음
        editingWord = null // 읽기 전용이므로 편집 기능 없음
    )

    val callbacks = WordCallbacks(
        onNavigateBack = onNavigateBack,
        onLevelSelected = { wordListViewModel.setSelectedLevel(it) },
        onSearchQueryChanged = { 
            searchQuery = it
            wordListViewModel.searchWords(it)
        },
        onToggleSearch = { showWordSearch = !showWordSearch },
        onShowAddDialog = { }, // 읽기 전용이므로 비활성화
        onDismissAddDialog = { },
        onEditWord = { }, // 읽기 전용이므로 비활성화
        onDismissEditDialog = { },
        onDeleteWord = { } // 읽기 전용이므로 비활성화
    )

    WordListLayout(
        state = state,
        callbacks = callbacks,
        viewModel = null, // 읽기 전용이므로 null
        modifier = modifier,
        isReadOnly = true // 읽기 전용 모드
    )
}

@Preview(showBackground = true)
@Composable
fun WordListScreenPreview() {
    YomiYomiTheme {
        WordListScreen(
            onNavigateBack = {},
            wordListViewModel = DummyWordViewModel()
        )
    }
} 

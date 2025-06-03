package com.lass.yomiyomi.ui.screen.basic.kanji

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.MyKanjiItem
import com.lass.yomiyomi.ui.layout.KanjiListLayout
import com.lass.yomiyomi.ui.state.KanjiState
import com.lass.yomiyomi.ui.state.KanjiCallbacks
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.kanji.DummyKanjiViewModel
import com.lass.yomiyomi.viewmodel.kanji.KanjiViewModel
import com.lass.yomiyomi.viewmodel.kanji.KanjiViewModelInterface

@Composable
fun KanjiListScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    kanjiListViewModel: KanjiViewModelInterface = hiltViewModel<KanjiViewModel>()
) {
    val kanji by kanjiListViewModel.kanji.collectAsState()
    val isLoading by kanjiListViewModel.isLoading.collectAsState()
    val selectedLevel by kanjiListViewModel.selectedLevel.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showKanjiSearch by remember { mutableStateOf(false) }

    // 안드로이드 시스템 뒤로가기 버튼도 onNavigateBack과 같은 동작
    BackHandler { onNavigateBack() }

    val state = KanjiState(
        myKanji = kanji.map { kanjiItem ->
            // KanjiItem을 MyKanjiItem으로 변환 (호환성을 위해)
            MyKanjiItem(
                id = kanjiItem.id,
                kanji = kanjiItem.kanji,
                onyomi = kanjiItem.onyomi,
                kunyomi = kanjiItem.kunyomi,
                meaning = kanjiItem.meaning,
                level = kanjiItem.level,
                learningWeight = kanjiItem.learningWeight,
                timestamp = kanjiItem.timestamp
            )
        },
        isLoading = isLoading,
        selectedLevel = selectedLevel,
        searchQuery = searchQuery,
        showMyKanjiSearch = showKanjiSearch,
        showAddDialog = false, // 읽기 전용이므로 추가 기능 없음
        editingKanji = null // 읽기 전용이므로 편집 기능 없음
    )

    val callbacks = KanjiCallbacks(
        onNavigateBack = onNavigateBack,
        onLevelSelected = { kanjiListViewModel.setSelectedLevel(it) },
        onSearchQueryChanged = { 
            searchQuery = it
            kanjiListViewModel.searchKanji(it)
        },
        onToggleSearch = { showKanjiSearch = !showKanjiSearch },
        onShowAddDialog = { }, // 읽기 전용이므로 비활성화
        onDismissAddDialog = { },
        onEditKanji = { }, // 읽기 전용이므로 비활성화
        onDismissEditDialog = { },
        onDeleteKanji = { } // 읽기 전용이므로 비활성화
    )

    KanjiListLayout(
        state = state,
        callbacks = callbacks,
        viewModel = null, // 읽기 전용이므로 null
        modifier = modifier,
        isReadOnly = true // 읽기 전용 모드
    )
}

@Preview(showBackground = true)
@Composable
fun KanjiListScreenPreview() {
    YomiYomiTheme {
        KanjiListScreen(
            onNavigateBack = {},
            kanjiListViewModel = DummyKanjiViewModel()
        )
    }
} 

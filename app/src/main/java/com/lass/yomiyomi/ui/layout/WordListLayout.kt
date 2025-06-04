package com.lass.yomiyomi.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.component.button.LevelSelector
import com.lass.yomiyomi.ui.component.card.WordCard
import com.lass.yomiyomi.ui.component.dialog.input.AddWordDialog
import com.lass.yomiyomi.ui.component.dialog.edit.EditWordDialog
import com.lass.yomiyomi.ui.state.WordState
import com.lass.yomiyomi.ui.state.WordCallbacks
import com.lass.yomiyomi.viewmodel.myWord.list.MyWordViewModelInterface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListLayout(
    state: WordState,
    callbacks: WordCallbacks,
    viewModel: MyWordViewModelInterface? = null,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    title: String = if (isReadOnly) "단어 학습" else "내 단어"
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = callbacks.onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "뒤로가기",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = callbacks.onToggleSearch) {
                        Icon(
                            Icons.Default.Search, 
                            contentDescription = "검색",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    // 읽기 전용 모드가 아닐 때만 추가 버튼 표시
                    if (!isReadOnly) {
                        IconButton(onClick = callbacks.onShowAddDialog) {
                            Icon(
                                Icons.Default.Add, 
                                contentDescription = "단어 추가",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 단어 검색
            if (state.showMyWordSearch) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = callbacks.onSearchQueryChanged,
                    label = { Text(if (isReadOnly) "단어 검색" else "내 단어 검색") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 레벨 필터
            LevelSelector(
                selectedLevel = state.selectedLevel,
                onLevelSelected = callbacks.onLevelSelected,
                availableLevels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 단어 목록
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.myWords.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isReadOnly) {
                            "단어가 없습니다."
                        } else {
                            "내 단어가 없습니다.\n+ 버튼을 눌러 단어를 추가해보세요!"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.myWords) { myWord ->
                        WordCard(
                            word = myWord,
                            // 읽기 전용 모드가 아닐 때만 편집/삭제 기능 제공
                            onEdit = if (!isReadOnly) { { callbacks.onEditWord(myWord) } } else null,
                            onDelete = if (!isReadOnly) { { callbacks.onDeleteWord(myWord) } } else null
                        )
                    }
                }
            }
        }

        // 읽기 전용 모드가 아닐 때만 다이얼로그들 표시
        if (!isReadOnly && viewModel != null) {
            // 단어 추가 다이얼로그
            if (state.showAddDialog) {
                AddWordDialog(
                    viewModel = viewModel,
                    onDismiss = callbacks.onDismissAddDialog
                )
            }

            // 단어 수정 다이얼로그
            state.editingWord?.let { myWord ->
                EditWordDialog(
                    myWord = myWord,
                    viewModel = viewModel,
                    onDismiss = callbacks.onDismissEditDialog
                )
            }
        }
    }
}

// 호환성을 위한 별칭
@Composable
fun MyWordLayout(
    state: WordState,
    callbacks: WordCallbacks,
    viewModel: MyWordViewModelInterface,
    modifier: Modifier = Modifier
) {
    WordListLayout(
        state = state,
        callbacks = callbacks,
        viewModel = viewModel,
        modifier = modifier,
        isReadOnly = false
    )
} 

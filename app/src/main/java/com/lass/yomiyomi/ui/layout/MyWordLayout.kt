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
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.ui.component.common.LevelSelector
import com.lass.yomiyomi.ui.component.my.MyWordCard
import com.lass.yomiyomi.ui.component.my.AddWordDialog
import com.lass.yomiyomi.ui.component.my.EditWordDialog
import com.lass.yomiyomi.ui.state.MyWordState
import com.lass.yomiyomi.ui.state.MyWordCallbacks
import com.lass.yomiyomi.viewmodel.myWord.MyWordViewModelInterface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWordLayout(
    state: MyWordState,
    callbacks: MyWordCallbacks,
    viewModel: MyWordViewModelInterface,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "내 단어",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = callbacks.onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = callbacks.onToggleSearch) {
                        Icon(Icons.Default.Search, contentDescription = "검색")
                    }
                    IconButton(onClick = callbacks.onShowAddDialog) {
                        Icon(Icons.Default.Add, contentDescription = "단어 추가")
                    }
                }
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
            // 내 단어 검색
            if (state.showMyWordSearch) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = callbacks.onSearchQueryChanged,
                    label = { Text("내 단어 검색") },
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

            // 내 단어 목록
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
                        "내 단어가 없습니다.\n+ 버튼을 눌러 단어를 추가해보세요!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.myWords) { myWord ->
                        MyWordCard(
                            myWord = myWord,
                            onEdit = { callbacks.onEditWord(myWord) },
                            onDelete = { callbacks.onDeleteWord(myWord) }
                        )
                    }
                }
            }
        }

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
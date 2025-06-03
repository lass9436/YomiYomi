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
import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.ui.component.common.LevelSelector
import com.lass.yomiyomi.ui.component.list.MyKanjiCard
import com.lass.yomiyomi.ui.component.list.AddKanjiDialog
import com.lass.yomiyomi.ui.component.list.EditKanjiDialog
import com.lass.yomiyomi.ui.state.MyKanjiState
import com.lass.yomiyomi.ui.state.MyKanjiCallbacks
import com.lass.yomiyomi.viewmodel.myKanji.MyKanjiViewModelInterface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyKanjiLayout(
    state: MyKanjiState,
    callbacks: MyKanjiCallbacks,
    viewModel: MyKanjiViewModelInterface,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "내 한자",
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
                        Icon(Icons.Default.Add, contentDescription = "한자 추가")
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
            // 내 한자 검색
            if (state.showMyKanjiSearch) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = callbacks.onSearchQueryChanged,
                    label = { Text("내 한자 검색") },
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

            // 내 한자 목록
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.myKanji.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "내 한자가 없습니다.\n+ 버튼을 눌러 한자를 추가해보세요!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.myKanji) { myKanji ->
                        MyKanjiCard(
                            myKanji = myKanji,
                            onEdit = { callbacks.onEditKanji(myKanji) },
                            onDelete = { callbacks.onDeleteKanji(myKanji) }
                        )
                    }
                }
            }
        }

        // 한자 추가 다이얼로그
        if (state.showAddDialog) {
            AddKanjiDialog(
                viewModel = viewModel,
                onDismiss = callbacks.onDismissAddDialog
            )
        }

        // 한자 수정 다이얼로그
        state.editingKanji?.let { myKanji ->
            EditKanjiDialog(
                myKanji = myKanji,
                viewModel = viewModel,
                onDismiss = callbacks.onDismissEditDialog
            )
        }
    }
} 

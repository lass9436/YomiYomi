package com.lass.yomiyomi.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lass.yomiyomi.ui.component.common.LevelSelector
import com.lass.yomiyomi.ui.component.my.MyWordCard
import com.lass.yomiyomi.ui.component.my.AddWordDialog
import com.lass.yomiyomi.viewmodel.myWord.MyWordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWordScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: MyWordViewModel = viewModel { MyWordViewModel(context) }
    
    val myWords by viewModel.myWords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showMyWordSearch by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("내 단어") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                }
            },
            actions = {
                IconButton(onClick = { showMyWordSearch = !showMyWordSearch }) {
                    Icon(Icons.Default.Search, contentDescription = "검색")
                }
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "단어 추가")
                }
            }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // 내 단어 검색
            if (showMyWordSearch) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        viewModel.searchMyWords(it)
                    },
                    label = { Text("내 단어 검색") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 레벨 필터
            LevelSelector(
                selectedLevel = selectedLevel,
                onLevelSelected = { viewModel.setSelectedLevel(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 내 단어 목록
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (myWords.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("내 단어가 없습니다.\n+ 버튼을 눌러 단어를 추가해보세요!")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(myWords) { myWord ->
                        MyWordCard(
                            myWord = myWord,
                            onDelete = { viewModel.deleteMyWord(myWord) }
                        )
                    }
                }
            }
        }
    }

    // 단어 추가 다이얼로그
    if (showAddDialog) {
        AddWordDialog(
            viewModel = viewModel,
            onDismiss = { showAddDialog = false }
        )
    }
} 

package com.lass.yomiyomi.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lass.yomiyomi.data.model.MyWord
import com.lass.yomiyomi.data.model.Word
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
            LevelFilterRow(
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

@Composable
fun LevelFilterRow(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    val levels = listOf("ALL", "N1", "N2", "N3", "N4", "N5")
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(levels) { level ->
            FilterChip(
                selected = selectedLevel == level,
                onClick = { onLevelSelected(level) },
                label = { Text(level) }
            )
        }
    }
}

@Composable
fun MyWordCard(
    myWord: MyWord,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = myWord.word,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = myWord.reading,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = myWord.meaning,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${myWord.type} • ${myWord.level}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddWordDialog(
    viewModel: MyWordViewModel,
    onDismiss: () -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var localSearchQuery by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("단어 추가") },
        text = {
            Column {
                OutlinedTextField(
                    value = localSearchQuery,
                    onValueChange = { 
                        localSearchQuery = it
                        viewModel.searchOriginalWords(it)
                    },
                    label = { Text("단어 검색") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (searchResults.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { word ->
                            SearchResultCard(
                                word = word,
                                onAdd = { 
                                    viewModel.addWordToMyWords(word) { success ->
                                        if (success) {
                                            onDismiss()
                                        }
                                    }
                                }
                            )
                        }
                    }
                } else if (localSearchQuery.isNotBlank()) {
                    Text("검색 결과가 없습니다.")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        }
    )
}

@Composable
fun SearchResultCard(
    word: Word,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = word.reading,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = word.meaning,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${word.type} • ${word.level}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(
                onClick = onAdd,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("추가")
            }
        }
    }
} 

package com.lass.yomiyomi.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lass.yomiyomi.domain.model.SentenceItem
import com.lass.yomiyomi.ui.component.*
import com.lass.yomiyomi.ui.layout.SentenceListLayout
import com.lass.yomiyomi.viewmodel.sentence.SentenceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceListScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SentenceViewModel = hiltViewModel()
) {
    // ViewModel 상태 수집
    val sentences by viewModel.sentences.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    
    // 검색 쿼리는 로컬 상태로 관리
    var searchQuery by remember { mutableStateOf("") }
    
    // UI 상태
    var displayMode by remember { mutableStateOf(com.lass.yomiyomi.ui.component.DisplayMode.FULL) }
    var showKorean by remember { mutableStateOf(true) }
    var showProgress by remember { mutableStateOf(true) }
    var showInputDialog by remember { mutableStateOf(false) }
    var editingSentence by remember { mutableStateOf<SentenceItem?>(null) }
    var deletingSentence by remember { mutableStateOf<SentenceItem?>(null) }
    
    // 검색 쿼리 변경 시 ViewModel에 전달
    LaunchedEffect(searchQuery) {
        viewModel.searchSentences(searchQuery)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("문장 학습") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        SentenceListLayout(
            sentences = sentences,
            isLoading = isLoading,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            selectedCategory = if (selectedCategory == "ALL") "전체" else selectedCategory,
            onCategoryChange = { category ->
                viewModel.setSelectedCategory(if (category == "전체") "ALL" else category)
            },
            displayMode = displayMode,
            onDisplayModeChange = { displayMode = it },
            showKorean = showKorean,
            onShowKoreanChange = { showKorean = it },
            showProgress = showProgress,
            onShowProgressChange = { showProgress = it },
            onSentenceEdit = { sentence ->
                editingSentence = sentence
                showInputDialog = true
            },
            onSentenceDelete = { sentence ->
                deletingSentence = sentence
            },
            onAddSentence = {
                editingSentence = null
                showInputDialog = true
            },
            modifier = modifier.padding(paddingValues)
        )
    }
    
    // 문장 입력/편집 다이얼로그
    SentenceInputDialog(
        isOpen = showInputDialog,
        sentence = editingSentence,
        onDismiss = {
            showInputDialog = false
            editingSentence = null
        },
        onSave = { sentence ->
            if (editingSentence != null) {
                // 편집 모드
                viewModel.updateSentence(sentence)
            } else {
                // 새로 추가 모드
                viewModel.insertSentence(sentence)
            }
            showInputDialog = false
            editingSentence = null
        }
    )
    
    // 삭제 확인 다이얼로그
    deletingSentence?.let { sentence ->
        AlertDialog(
            onDismissRequest = { deletingSentence = null },
            title = { Text("문장 삭제") },
            text = { 
                Text("이 문장을 정말 삭제하시겠습니까?\n\n${sentence.japanese}")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSentence(sentence.id)
                        deletingSentence = null
                    }
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingSentence = null }) {
                    Text("취소")
                }
            }
        )
    }
} 
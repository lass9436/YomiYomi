package com.lass.yomiyomi.ui.screen.my.sentence

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.ui.component.dialog.input.SentenceInputDialog
import com.lass.yomiyomi.ui.layout.SentenceListLayout
import com.lass.yomiyomi.viewmodel.mySentence.MySentenceViewModel
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceListScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MySentenceViewModel = hiltViewModel()
) {
    // Android 뒤로가기 버튼 처리
    BackHandler {
        onBack()
    }

    // ViewModel 상태 수집
    val sentences by viewModel.sentences.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val availableCategories by viewModel.availableCategories.collectAsStateWithLifecycle()
    val availableDifficulties by viewModel.availableDifficulties.collectAsStateWithLifecycle()
    
    // 검색 쿼리는 로컬 상태로 관리
    var searchQuery by remember { mutableStateOf("") }
    
    // UI 상태
    var displayMode by remember { mutableStateOf(DisplayMode.FULL) }
    var showKorean by remember { mutableStateOf(true) }
    var showProgress by remember { mutableStateOf(true) }
    var showInputDialog by remember { mutableStateOf(false) }
    var editingSentence by remember { mutableStateOf<SentenceItem?>(null) }
    var deletingSentence by remember { mutableStateOf<SentenceItem?>(null) }
    var isFilterVisible by remember { mutableStateOf(false) } // 필터 표시 상태
    
    // 검색 쿼리 변경 시 ViewModel에 전달
    LaunchedEffect(searchQuery) {
        viewModel.searchSentences(searchQuery)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "문장 학습",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "뒤로 가기",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isFilterVisible = !isFilterVisible }
                    ) {
                        Icon(
                            Icons.Default.Settings, 
                            contentDescription = "필터 토글",
                            tint = if (isFilterVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                        )
                    }
                    IconButton(
                        onClick = {
                            editingSentence = null
                            showInputDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Add, 
                            contentDescription = "문장 추가",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
            isFilterVisible = isFilterVisible,
            onSentenceEdit = { sentence ->
                editingSentence = sentence
                showInputDialog = true
            },
            onSentenceDelete = { sentence ->
                deletingSentence = sentence
            },
            modifier = modifier.padding(paddingValues)
        )
    }
    
    // 문장 입력/편집 다이얼로그
    SentenceInputDialog(
        isOpen = showInputDialog,
        sentence = editingSentence,
        availableCategories = availableCategories,
        availableDifficulties = availableDifficulties,
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

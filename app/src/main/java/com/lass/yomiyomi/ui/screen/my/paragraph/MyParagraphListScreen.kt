package com.lass.yomiyomi.ui.screen.my.paragraph

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lass.yomiyomi.domain.model.ParagraphItem
import com.lass.yomiyomi.ui.component.*
import com.lass.yomiyomi.ui.layout.ParagraphListLayout
import com.lass.yomiyomi.viewmodel.myParagraph.MyParagraphViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphListScreen(
    onBack: () -> Unit,
    onParagraphClick: (ParagraphItem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyParagraphViewModel = hiltViewModel()
) {
    // Android 뒤로가기 버튼 처리
    BackHandler {
        onBack()
    }

    // ViewModel 상태 수집
    val paragraphs by viewModel.paragraphs.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    
    // 검색 쿼리는 로컬 상태로 관리
    var searchQuery by remember { mutableStateOf("") }
    
    // UI 상태
    var showInputDialog by remember { mutableStateOf(false) }
    var editingParagraph by remember { mutableStateOf<ParagraphItem?>(null) }
    var deletingParagraph by remember { mutableStateOf<ParagraphItem?>(null) }
    
    // 검색 쿼리 변경 시 ViewModel에 전달
    LaunchedEffect(searchQuery) {
        viewModel.searchParagraphs(searchQuery)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("문단 학습") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            editingParagraph = null
                            showInputDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "문단 추가")
                    }
                }
            )
        }
    ) { paddingValues ->
        ParagraphListLayout(
            paragraphs = paragraphs,
            sentenceCounts = emptyMap(), // 임시로 빈 맵 사용
            isLoading = isLoading,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            selectedCategory = if (selectedCategory == "ALL") "전체" else selectedCategory,
            onCategoryChange = { category ->
                viewModel.setSelectedCategory(if (category == "전체") "ALL" else category)
            },
            onParagraphClick = onParagraphClick,
            onParagraphEdit = { paragraph ->
                editingParagraph = paragraph
                showInputDialog = true
            },
            onParagraphDelete = { paragraph ->
                deletingParagraph = paragraph
            },
            modifier = modifier.padding(paddingValues)
        )
    }
    
    // 문단 입력/편집 다이얼로그
    ParagraphInputDialog(
        isOpen = showInputDialog,
        paragraph = editingParagraph,
        onDismiss = {
            showInputDialog = false
            editingParagraph = null
        },
        onSave = { paragraph ->
            if (editingParagraph != null) {
                // 편집 모드
                viewModel.updateParagraph(paragraph)
            } else {
                // 새로 추가 모드
                viewModel.insertParagraph(paragraph)
            }
            showInputDialog = false
            editingParagraph = null
        }
    )
    
    // 삭제 확인 다이얼로그
    deletingParagraph?.let { paragraph ->
        AlertDialog(
            onDismissRequest = { deletingParagraph = null },
            title = { Text("문단 삭제") },
            text = { 
                Text("이 문단을 정말 삭제하시겠습니까?\n포함된 모든 문장도 함께 삭제됩니다.\n\n${paragraph.title}")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteParagraph(paragraph.paragraphId)
                        deletingParagraph = null
                    }
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingParagraph = null }) {
                    Text("취소")
                }
            }
        )
    }
} 
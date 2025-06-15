package com.lass.yomiyomi.ui.screen.my.paragraph

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
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.ui.component.dialog.input.ParagraphInputDialog
import com.lass.yomiyomi.ui.layout.ParagraphListLayout
import com.lass.yomiyomi.viewmodel.myParagraph.list.MyParagraphListViewModel
import androidx.compose.ui.text.font.FontWeight

// (1) 추가: 문단 리스트에 추가/편집/삭제 다이얼로그용 임포트
import com.lass.yomiyomi.ui.component.dialog.list.ParagraphListDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphListScreen(
    onBack: () -> Unit,
    onParagraphClick: (ParagraphItem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyParagraphListViewModel = hiltViewModel()
) {
    BackHandler { onBack() }

    val paragraphs by viewModel.paragraphs.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val learningProgress by viewModel.learningProgress.collectAsStateWithLifecycle()
    val sentenceCounts by viewModel.sentenceCounts.collectAsStateWithLifecycle()
    val sentencesMap by viewModel.sentencesMap.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    var showInputDialog by remember { mutableStateOf(false) }
    var editingParagraph by remember { mutableStateOf<ParagraphItem?>(null) }
    var deletingParagraph by remember { mutableStateOf<ParagraphItem?>(null) }
    var isFilterVisible by remember { mutableStateOf(false) }

    // (2) 추가: 문단-리스트 추가용 상태
    var showAddToListDialog by remember { mutableStateOf(false) }
    var targetParagraphForAdd by remember { mutableStateOf<ParagraphItem?>(null) }
    val paragraphLists by viewModel.paragraphLists.collectAsStateWithLifecycle() // ViewModel에서 리스트 컬렉션 호출
    val checkedListIds = remember { mutableStateListOf<Long>() } // 체크된 리스트 id들

    LaunchedEffect(searchQuery) {
        viewModel.searchParagraphs(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "문단 학습",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
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
                            editingParagraph = null
                            showInputDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "문단 추가",
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
        // (3) ParagraphListLayout에 "onAddToListClick" 추가
        ParagraphListLayout(
            paragraphs = paragraphs,
            sentenceCounts = sentenceCounts,
            learningProgress = learningProgress,
            sentencesMap = sentencesMap,
            isLoading = isLoading,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            selectedCategory = if (selectedCategory == "ALL") "전체" else selectedCategory,
            onCategoryChange = { category ->
                viewModel.setSelectedCategory(if (category == "전체") "ALL" else category)
            },
            isFilterVisible = isFilterVisible,
            onParagraphClick = onParagraphClick,
            onParagraphEdit = { paragraph ->
                editingParagraph = paragraph
                showInputDialog = true
            },
            onParagraphDelete = { paragraph ->
                deletingParagraph = paragraph
            },
            // (4) 새로운 파라미터: +버튼 눌렀을 때
            onAddToListClick = { paragraph ->
                targetParagraphForAdd = paragraph
                checkedListIds.clear()
                showAddToListDialog = true
            },
            modifier = modifier.padding(paddingValues)
        )
    }

    ParagraphInputDialog(
        isOpen = showInputDialog,
        paragraph = editingParagraph,
        onDismiss = {
            showInputDialog = false
            editingParagraph = null
        },
        onSave = { paragraph ->
            if (editingParagraph != null) {
                viewModel.updateParagraph(paragraph)
            } else {
                viewModel.insertParagraph(paragraph)
            }
            showInputDialog = false
            editingParagraph = null
        }
    )

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

    // (5) 문단-리스트 추가용 다이얼로그
    if (showAddToListDialog && targetParagraphForAdd != null) {
        ParagraphListDialog(
            paragraphLists = paragraphLists,
            checkedListIds = checkedListIds,
            onCheckedChange = { listId, checked ->
                if (checked) {
                    if (listId !in checkedListIds) checkedListIds.add(listId)
                } else {
                    checkedListIds.remove(listId)
                }
            },
            onAddListClick = { name ->
                viewModel.addNewParagraphList(name)
            },
            onEditListClick = { listId, newName ->
                viewModel.renameParagraphList(listId, newName)
            },
            onDeleteListClick = { listId ->
                viewModel.deleteParagraphList(listId)
            },
            onDismiss = {
                showAddToListDialog = false
                targetParagraphForAdd = null
            },
            onConfirm = {
                viewModel.addParagraphToLists(
                    paragraph = targetParagraphForAdd!!,
                    listIds = checkedListIds.toList()
                )
                showAddToListDialog = false
                targetParagraphForAdd = null
            }
        )
    }
}

package com.lass.yomiyomi.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.ui.component.card.ParagraphHeaderCard
import com.lass.yomiyomi.ui.component.card.ParagraphSentenceCard
import com.lass.yomiyomi.ui.component.loading.LoadingIndicator
import com.lass.yomiyomi.ui.component.empty.EmptyView
import com.lass.yomiyomi.ui.component.text.tts.UnifiedTTSButton
import com.lass.yomiyomi.ui.component.dialog.input.SentenceInputDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyParagraphDetailLayout(
    paragraph: ParagraphItem?,
    sentences: List<SentenceItem>,
    isLoading: Boolean,
    displayMode: DisplayMode = DisplayMode.FULL,
    showKorean: Boolean = true,
    onBack: () -> Unit,
    onSaveSentence: (SentenceItem, Boolean) -> Unit, // sentence, isEdit
    onDeleteSentence: (Int) -> Unit, // sentenceId
    onQuiz: (() -> Unit)? = null, // 퀴즈 시작 콜백 추가
    modifier: Modifier = Modifier
) {
    // Layout 내부 UI 상태
    var showInputDialog by remember { mutableStateOf(false) }
    var editingSentence by remember { mutableStateOf<SentenceItem?>(null) }
    var deletingSentence by remember { mutableStateOf<SentenceItem?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        paragraph?.title ?: "문단 상세",
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
                    // 전체 문장 읽기 버튼
                    if (sentences.isNotEmpty()) {
                        val allJapaneseText = sentences
                            .sortedBy { it.orderInParagraph }
                            .joinToString("。") { it.japanese.replace(Regex("\\[.*?\\]"), "") }
                            .plus("。")
                        
                        UnifiedTTSButton(
                            text = allJapaneseText,
                            size = 24.dp
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 문단 정보 헤더
            paragraph?.let { para ->
                ParagraphHeaderCard(
                    paragraph = para,
                    sentenceCount = sentences.size,
                    sentences = sentences,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // 문장 목록
            if (isLoading) {
                LoadingIndicator(modifier = Modifier.weight(1f))
            } else if (sentences.isEmpty()) {
                EmptyView(
                    title = "문장이 없습니다",
                    subtitle = "오른쪽 위 + 버튼을 눌러 문장을 추가해보세요!",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(sentences) { sentence ->
                        ParagraphSentenceCard(
                            sentence = sentence,
                            displayMode = displayMode,
                            showKorean = showKorean,
                            onEdit = {
                                editingSentence = sentence
                                showInputDialog = true
                            },
                            onDelete = {
                                deletingSentence = sentence
                            }
                        )
                    }
                }
            }
            
            // 문제 풀기 버튼 추가
            onQuiz?.let { quizCallback ->
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = quizCallback,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "문제 풀기",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // 문장 입력/편집 다이얼로그
    SentenceInputDialog(
        isOpen = showInputDialog,
        sentence = editingSentence,
        availableCategories = emptyList(), // 문단 소속 문장은 카테고리 선택 불가
        availableLevels = emptyList(), // 문단 소속 문장은 레벨 선택 불가
        onDismiss = {
            showInputDialog = false
            editingSentence = null
        },
        onSave = { sentence ->
            onSaveSentence(sentence, editingSentence != null)
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
                        onDeleteSentence(sentence.id)
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
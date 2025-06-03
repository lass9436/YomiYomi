package com.lass.yomiyomi.ui.screen.my.paragraph

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.ui.component.text.furigana.FuriganaText
import com.lass.yomiyomi.ui.component.loading.LoadingIndicator
import com.lass.yomiyomi.ui.component.empty.EmptyView
import com.lass.yomiyomi.ui.component.dialog.input.SentenceInputDialog
import com.lass.yomiyomi.ui.component.text.tts.UnifiedTTSButton
import com.lass.yomiyomi.util.rememberSpeechManager
import com.lass.yomiyomi.util.JapaneseTextFilter
import com.lass.yomiyomi.util.handleBackNavigation
import com.lass.yomiyomi.speech.SpeechManager
import com.lass.yomiyomi.viewmodel.myParagraph.MyParagraphDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphDetailScreen(
    paragraphId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyParagraphDetailViewModel = hiltViewModel()
) {
    // Android 뒤로가기 버튼 처리
    val speechManager = rememberSpeechManager()
    BackHandler { speechManager.handleBackNavigation(onBack) }
    
    // ViewModel 상태 수집
    val paragraph by viewModel.paragraph.collectAsStateWithLifecycle()
    val sentences by viewModel.sentences.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val availableCategories by viewModel.availableCategories.collectAsStateWithLifecycle()
    val availableDifficulties by viewModel.availableDifficulties.collectAsStateWithLifecycle()
    
    // UI 상태
    var showInputDialog by remember { mutableStateOf(false) }
    var editingSentence by remember { mutableStateOf<SentenceItem?>(null) }
    var deletingSentence by remember { mutableStateOf<SentenceItem?>(null) }
    var displayMode by remember { mutableStateOf(DisplayMode.FULL) }
    var showKorean by remember { mutableStateOf(true) }
    
    // paragraphId가 변경될 때마다 데이터 로드
    LaunchedEffect(paragraphId) {
        viewModel.loadParagraphDetail(paragraphId)
    }
    
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
                    IconButton(onClick = { speechManager.handleBackNavigation(onBack) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "뒤로 가기",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                actions = {
                    // 전체 문장 읽기 버튼
                    paragraph?.let {
                        if (sentences.isNotEmpty()) {
                            val isSpeaking by speechManager.isSpeaking.collectAsState()
                            val currentSpeakingText by speechManager.currentSpeakingText.collectAsState()
                            val allJapanese = sentences.joinToString("。") { it.japanese }
                            val isThisParagraphSpeaking = isSpeaking && currentSpeakingText == allJapanese
                            
                            val rotation by animateFloatAsState(
                                targetValue = if (isThisParagraphSpeaking) 360f else 0f,
                                animationSpec = if (isThisParagraphSpeaking) {
                                    infiniteRepeatable(
                                        animation = tween(2000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Restart
                                    )
                                } else {
                                    tween(200)
                                },
                                label = "rotation"
                            )
                            
                            IconButton(
                                onClick = {
                                    if (isThisParagraphSpeaking) {
                                        speechManager.stopSpeaking()
                                    } else {
                                        val filteredText = JapaneseTextFilter.prepareTTSText(allJapanese)
                                        if (filteredText.isNotEmpty()) {
                                            speechManager.speakWithOriginalText(allJapanese, filteredText, "paragraph_all")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    if (isThisParagraphSpeaking) Icons.Default.Close else Icons.Default.PlayArrow,
                                    contentDescription = if (isThisParagraphSpeaking) "전체 문장 읽기 중지" else "전체 문장 읽기",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.graphicsLayer(
                                        rotationZ = rotation
                                    )
                                )
                            }
                        }
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
            // 문단 정보 (읽기 전용)
            paragraph?.let { para ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = para.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (para.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = para.description,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            AssistChip(
                                onClick = { },
                                label = { Text(para.category, fontSize = 12.sp) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AssistChip(
                                onClick = { },
                                label = { Text(para.difficulty, fontSize = 12.sp) }
                            )
                        }
                    }
                }
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
                    verticalArrangement = Arrangement.spacedBy(0.dp) // 간격 없애서 자연스럽게
                ) {
                    items(sentences) { sentence ->
                        ParagraphSentenceItem(
                            sentence = sentence,
                            displayMode = displayMode,
                            showKorean = showKorean,
                            speechManager = speechManager,
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
        }
    }
    
    // 문장 입력/편집 다이얼로그
    SentenceInputDialog(
        isOpen = showInputDialog,
        sentence = editingSentence,
        availableCategories = emptyList(), // 문단 소속 문장은 카테고리 선택 불가
        availableDifficulties = emptyList(), // 문단 소속 문장은 난이도 선택 불가
        onDismiss = {
            showInputDialog = false
            editingSentence = null
        },
        onSave = { sentence ->
            // 문단 ID와 문단의 카테고리/난이도 설정
            val sentenceWithParagraph = sentence.copy(
                paragraphId = paragraphId,
                category = paragraph?.category ?: "",
                difficulty = paragraph?.difficulty ?: ""
            )
            
            if (editingSentence != null) {
                viewModel.updateSentence(sentenceWithParagraph)
            } else {
                viewModel.insertSentence(sentenceWithParagraph)
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

@Composable
private fun ParagraphSentenceItem(
    sentence: SentenceItem,
    displayMode: DisplayMode,
    showKorean: Boolean,
    speechManager: SpeechManager,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 카드 모양이지만 border 없이 자연스럽게
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp), // border radius 제거
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // 그림자 제거
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 일본어
            if (displayMode != DisplayMode.KOREAN_ONLY) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FuriganaText(
                        japaneseText = sentence.japanese,
                        displayMode = displayMode,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    
                    UnifiedTTSButton(
                        text = sentence.japanese,
                        speechManager = speechManager,
                        size = 24.dp
                    )
                }
            }
            
            // 한국어 + 인라인 버튼들
            if ((showKorean && displayMode != DisplayMode.JAPANESE_ONLY && displayMode != DisplayMode.JAPANESE_NO_FURIGANA) || displayMode == DisplayMode.KOREAN_ONLY) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = sentence.korean,
                        fontSize = if (displayMode == DisplayMode.KOREAN_ONLY) 18.sp else 16.sp,
                        color = if (displayMode == DisplayMode.KOREAN_ONLY) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // 편집/삭제 버튼들을 문장 끝에 inline으로
                    Row {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "편집",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "삭제",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            } else {
                // Korean이 안 보이는 모드일 때는 일본어 아래에 버튼들 배치
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "편집",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "삭제",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
} 

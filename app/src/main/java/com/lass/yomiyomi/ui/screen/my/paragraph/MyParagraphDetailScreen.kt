package com.lass.yomiyomi.ui.screen.my.paragraph

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.layout.MyParagraphDetailLayout
import com.lass.yomiyomi.viewmodel.myParagraph.detail.MyParagraphDetailViewModel

@Composable
fun ParagraphDetailScreen(
    paragraphId: String,
    onBack: () -> Unit,
    viewModel: MyParagraphDetailViewModel = hiltViewModel()
) {
    // ViewModel 상태 수집
    val paragraph by viewModel.paragraph.collectAsStateWithLifecycle()
    val sentences by viewModel.sentences.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    // UI 상태
    var displayMode by remember { mutableStateOf(DisplayMode.FULL) }
    var showKorean by remember { mutableStateOf(true) }
    
    // paragraphId가 변경될 때마다 데이터 로드
    LaunchedEffect(paragraphId) {
        viewModel.loadParagraphDetail(paragraphId)
    }
    
    MyParagraphDetailLayout(
        paragraph = paragraph,
        sentences = sentences,
        isLoading = isLoading,
        displayMode = displayMode,
        showKorean = showKorean,
        onBack = onBack,
        onSaveSentence = { sentence, isEdit ->
            // 문단 ID와 문단의 카테고리/레벨 설정
            val sentenceWithParagraph = sentence.copy(
                paragraphId = paragraphId,
                category = paragraph?.category ?: "",
                level = paragraph?.level ?: Level.N5
            )
            
            if (isEdit) {
                viewModel.updateSentence(sentenceWithParagraph)
            } else {
                viewModel.insertSentence(sentenceWithParagraph)
            }
        },
        onDeleteSentence = { sentenceId ->
            viewModel.deleteSentence(sentenceId)
        }
    )
} 

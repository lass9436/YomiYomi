package com.lass.yomiyomi.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.ui.component.card.SentenceCard
import com.lass.yomiyomi.ui.component.search.SearchTextField
import com.lass.yomiyomi.ui.component.filter.SentenceFilterPanel
import com.lass.yomiyomi.ui.component.loading.LoadingIndicator
import com.lass.yomiyomi.ui.component.empty.EmptyView
import com.lass.yomiyomi.ui.component.button.BackgroundTTSButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceListLayout(
    sentences: List<SentenceItem>,
    isLoading: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    selectedCategory: String = "전체",
    onCategoryChange: (String) -> Unit = {},
    displayMode: DisplayMode = DisplayMode.FULL,
    onDisplayModeChange: (DisplayMode) -> Unit = {},
    showKorean: Boolean = true,
    onShowKoreanChange: (Boolean) -> Unit = {},
    showProgress: Boolean = true,
    onShowProgressChange: (Boolean) -> Unit = {},
    isFilterVisible: Boolean = false,
    onSentenceEdit: ((SentenceItem) -> Unit)? = null,
    onSentenceDelete: ((SentenceItem) -> Unit)? = null,
    onSentenceQuiz: ((SentenceItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categories = remember(sentences) {
        listOf("전체") + sentences.map { it.category }.distinct().sorted()
    }
    
    Column(modifier = modifier) {
        // 검색 바
        if (isFilterVisible) {
            SearchTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = "문장 검색",
                placeholder = "일본어나 한국어로 검색하세요"
            )
            
            // 필터 및 옵션 컨트롤
            SentenceFilterPanel(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategoryChange = onCategoryChange,
                displayMode = displayMode,
                onDisplayModeChange = onDisplayModeChange,
                showKorean = showKorean,
                onShowKoreanChange = onShowKoreanChange,
                showProgress = showProgress,
                onShowProgressChange = onShowProgressChange
            )
        }
        
        // 결과 개수 표시
        Text(
            text = "${sentences.size}개의 문장",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 백그라운드 TTS 버튼
        if (sentences.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                BackgroundTTSButton(
                    sentences = sentences,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // 문장 목록
        if (isLoading) {
            LoadingIndicator(modifier = Modifier.weight(1f))
        } else if (sentences.isEmpty()) {
            EmptyView(
                title = if (searchQuery.isNotEmpty() || selectedCategory != "전체") {
                    "검색 결과가 없습니다"
                } else {
                    "아직 문장이 없습니다"
                },
                subtitle = if (searchQuery.isEmpty() && selectedCategory == "전체") {
                    "오른쪽 위 + 버튼을 눌러 문장을 추가해보세요!"
                } else null,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sentences) { sentence ->
                    SentenceCard(
                        sentence = sentence,
                        displayMode = displayMode,
                        showKorean = showKorean,
                        showProgress = showProgress,
                        onEdit = onSentenceEdit?.let { { it(sentence) } },
                        onDelete = onSentenceDelete?.let { { it(sentence) } },
                        onQuiz = onSentenceQuiz?.let { { it(sentence) } }
                    )
                }
            }
        }
    }
} 

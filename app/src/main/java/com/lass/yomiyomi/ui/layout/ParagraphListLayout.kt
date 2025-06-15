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
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.ui.component.card.ParagraphCard
import com.lass.yomiyomi.ui.component.search.SearchTextField
import com.lass.yomiyomi.ui.component.filter.ParagraphFilterPanel
import com.lass.yomiyomi.ui.component.loading.LoadingIndicator
import com.lass.yomiyomi.ui.component.empty.EmptyView
import com.lass.yomiyomi.ui.component.button.BackgroundTTSButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphListLayout(
    paragraphs: List<ParagraphItem>,
    sentenceCounts: Map<Int, Int> = emptyMap(),
    learningProgress: Map<Int, Float> = emptyMap(),
    sentencesMap: Map<Int, List<SentenceItem>> = emptyMap(),
    isLoading: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    selectedCategory: String = "전체",
    onCategoryChange: (String) -> Unit = {},
    isFilterVisible: Boolean = false,
    onParagraphClick: ((ParagraphItem) -> Unit)? = null,
    onParagraphEdit: ((ParagraphItem) -> Unit)? = null,
    onParagraphDelete: ((ParagraphItem) -> Unit)? = null,
    onAddToList: ((ParagraphItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categories = remember(paragraphs) {
        listOf("전체") + paragraphs.map { it.category }.distinct().sorted()
    }
    
    Column(modifier = modifier) {
        // 검색 바
        if (isFilterVisible) {
            SearchTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = "문단 검색",
                placeholder = "제목이나 설명으로 검색하세요"
            )
        }
        
        // 카테고리 필터
        if (isFilterVisible) {
            ParagraphFilterPanel(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategoryChange = onCategoryChange
            )
        }
        
        // 결과 개수 표시
        Text(
            text = "${paragraphs.size}개의 문단",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 백그라운드 TTS 버튼
        if (paragraphs.isNotEmpty() && sentencesMap.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                BackgroundTTSButton(
                    paragraphs = paragraphs,
                    sentencesMap = sentencesMap,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // 문단 목록
        if (isLoading) {
            LoadingIndicator(modifier = Modifier.weight(1f))
        } else if (paragraphs.isEmpty()) {
            EmptyView(
                title = if (searchQuery.isNotEmpty() || selectedCategory != "전체") {
                    "검색 결과가 없습니다"
                } else {
                    "아직 문단이 없습니다"
                },
                subtitle = if (searchQuery.isEmpty() && selectedCategory == "전체") {
                    "오른쪽 위 + 버튼을 눌러 문단을 추가해보세요!"
                } else null,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(paragraphs) { paragraph ->
                    ParagraphCard(
                        paragraph = paragraph,
                        sentenceCount = sentenceCounts[paragraph.paragraphId] ?: 0,
                        learningProgress = learningProgress[paragraph.paragraphId] ?: 0f,
                        onClick = onParagraphClick?.let { { it(paragraph) } },
                        onEdit = onParagraphEdit?.let { { it(paragraph) } },
                        onDelete = onParagraphDelete?.let { { it(paragraph) } },
                        onAddToList = onAddToList?.let { { it(paragraph) } }
                    )
                }
            }
        }
    }
} 

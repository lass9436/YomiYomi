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
import com.lass.yomiyomi.ui.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphListLayout(
    paragraphs: List<ParagraphItem>,
    sentenceCounts: Map<String, Int> = emptyMap(),
    isLoading: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    selectedCategory: String = "전체",
    onCategoryChange: (String) -> Unit = {},
    onParagraphClick: ((ParagraphItem) -> Unit)? = null,
    onParagraphEdit: ((ParagraphItem) -> Unit)? = null,
    onParagraphDelete: ((ParagraphItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categories = remember(paragraphs) {
        listOf("전체") + paragraphs.map { it.category }.distinct().sorted()
    }
    
    Column(modifier = modifier) {
        // 검색 바
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("문단 검색") },
            placeholder = { Text("제목이나 설명으로 검색하세요") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 카테고리 필터
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "카테고리:",
                    fontSize = 14.sp,
                    modifier = Modifier.width(60.dp)
                )
                
                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { 
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    onCategoryChange(category)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // 결과 개수 표시
        Text(
            text = "${paragraphs.size}개의 문단",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 문단 목록
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (paragraphs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedCategory != "전체") {
                            "검색 결과가 없습니다"
                        } else {
                            "아직 문단이 없습니다"
                        },
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (searchQuery.isEmpty() && selectedCategory == "전체") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "오른쪽 위 + 버튼을 눌러 문단을 추가해보세요!",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
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
                        onClick = onParagraphClick?.let { { it(paragraph) } },
                        onEdit = onParagraphEdit?.let { { it(paragraph) } },
                        onDelete = onParagraphDelete?.let { { it(paragraph) } }
                    )
                }
            }
        }
    }
} 

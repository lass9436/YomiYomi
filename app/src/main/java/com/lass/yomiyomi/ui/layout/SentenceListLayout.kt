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
    onSentenceEdit: ((SentenceItem) -> Unit)? = null,
    onSentenceDelete: ((SentenceItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categories = remember(sentences) {
        listOf("전체") + sentences.map { it.category }.distinct().sorted()
    }
    
    Column(modifier = modifier) {
        // 검색 바
        SearchTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = "문장 검색",
            placeholder = "일본어나 한국어로 검색하세요"
        )
        
        // 필터 및 옵션 컨트롤
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 첫 번째 줄: 카테고리 필터
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 두 번째 줄: 표시 모드
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "표시:",
                        fontSize = 14.sp,
                        modifier = Modifier.width(60.dp)
                    )
                    
                    var displayExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = displayExpanded,
                        onExpandedChange = { displayExpanded = !displayExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = when (displayMode) {
                                DisplayMode.FULL -> "전체 표시"
                                DisplayMode.JAPANESE_ONLY -> "일본어만"
                                DisplayMode.FURIGANA_ONLY -> "요미가나만"
                                DisplayMode.KANJI_ONLY -> "한자만"
                            },
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { 
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = displayExpanded) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                        
                        ExposedDropdownMenu(
                            expanded = displayExpanded,
                            onDismissRequest = { displayExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("전체 표시") },
                                onClick = {
                                    onDisplayModeChange(DisplayMode.FULL)
                                    displayExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("일본어만") },
                                onClick = {
                                    onDisplayModeChange(DisplayMode.JAPANESE_ONLY)
                                    displayExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("요미가나만") },
                                onClick = {
                                    onDisplayModeChange(DisplayMode.FURIGANA_ONLY)
                                    displayExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("한자만") },
                                onClick = {
                                    onDisplayModeChange(DisplayMode.KANJI_ONLY)
                                    displayExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 세 번째 줄: 표시 옵션 체크박스들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showKorean,
                            onCheckedChange = onShowKoreanChange
                        )
                        Text("한국어", fontSize = 12.sp)
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showProgress,
                            onCheckedChange = onShowProgressChange
                        )
                        Text("학습진도", fontSize = 12.sp)
                    }
                }
            }
        }
        
        // 결과 개수 표시
        Text(
            text = "${sentences.size}개의 문장",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 문장 목록
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (sentences.isEmpty()) {
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
                            "아직 문장이 없습니다"
                        },
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (searchQuery.isEmpty() && selectedCategory == "전체") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "오른쪽 위 + 버튼을 눌러 문장을 추가해보세요!",
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
                items(sentences) { sentence ->
                    SentenceCard(
                        sentence = sentence,
                        displayMode = displayMode,
                        showKorean = showKorean,
                        showProgress = showProgress,
                        onEdit = onSentenceEdit?.let { { it(sentence) } },
                        onDelete = onSentenceDelete?.let { { it(sentence) } }
                    )
                }
            }
        }
    }
} 

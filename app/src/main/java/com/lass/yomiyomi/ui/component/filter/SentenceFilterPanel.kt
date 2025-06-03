package com.lass.yomiyomi.ui.component.filter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.constant.DisplayMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceFilterPanel(
    categories: List<String>,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit,
    showKorean: Boolean,
    onShowKoreanChange: (Boolean) -> Unit,
    showProgress: Boolean,
    onShowProgressChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
                            DisplayMode.JAPANESE_NO_FURIGANA -> "요미가나 없이"
                            DisplayMode.KOREAN_ONLY -> "한국어만"
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
                            text = { Text("요미가나 없이") },
                            onClick = {
                                onDisplayModeChange(DisplayMode.JAPANESE_NO_FURIGANA)
                                displayExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("한국어만") },
                            onClick = {
                                onDisplayModeChange(DisplayMode.KOREAN_ONLY)
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
} 
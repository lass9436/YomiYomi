package com.lass.yomiyomi.ui.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.ui.component.text.furigana.FuriganaText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceCard(
    sentence: SentenceItem,
    displayMode: DisplayMode = DisplayMode.FULL,
    showKorean: Boolean = true,
    showProgress: Boolean = true,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onDisplayModeChange: ((DisplayMode) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 상단: 일본어 (후리가나 포함) - KOREAN_ONLY 모드에서는 숨김
            if (displayMode != DisplayMode.KOREAN_ONLY) {
                FuriganaText(
                    japaneseText = sentence.japanese,
                    displayMode = displayMode,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // 한국어 번역 (showKorean이 true이고 일본어 전용 모드가 아니거나, KOREAN_ONLY 모드일 때)
            if ((showKorean && displayMode != DisplayMode.JAPANESE_ONLY && displayMode != DisplayMode.JAPANESE_NO_FURIGANA) || displayMode == DisplayMode.KOREAN_ONLY) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = sentence.korean,
                    fontSize = if (displayMode == DisplayMode.KOREAN_ONLY) 18.sp else 14.sp,
                    color = if (displayMode == DisplayMode.KOREAN_ONLY) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // 메타 정보
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 카테고리와 난이도
                Row {
                    AssistChip(
                        onClick = { },
                        label = { Text(sentence.category, fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            labelColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    AssistChip(
                        onClick = { },
                        label = { Text(sentence.difficulty, fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            labelColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
                
                // 편집/삭제 버튼들과 학습 진도
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 편집 버튼 (작은 아이콘 버튼)
                    onEdit?.let { editCallback ->
                        IconButton(
                            onClick = editCallback,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "편집",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    // 삭제 버튼 (작은 아이콘 버튼)
                    onDelete?.let { deleteCallback ->
                        IconButton(
                            onClick = deleteCallback,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "삭제",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    // 학습 진도 (showProgress가 true일 때만)
                    if (showProgress) {
                        Text(
                            text = "${(sentence.learningProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // 학습 진도 바 (showProgress가 true일 때만)
            if (showProgress) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { sentence.learningProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            // 표시 모드 변경 버튼만 별도로 (필요한 경우)
            onDisplayModeChange?.let { onChange ->
                Spacer(modifier = Modifier.height(8.dp))
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    AssistChip(
                        onClick = { expanded = true },
                        label = { Text("표시 모드", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                        modifier = Modifier.menuAnchor(),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            labelColor = MaterialTheme.colorScheme.tertiary,
                            leadingIconContentColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("전체 표시") },
                            onClick = {
                                onChange(DisplayMode.FULL)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("일본어만") },
                            onClick = {
                                onChange(DisplayMode.JAPANESE_ONLY)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("요미가나 없이") },
                            onClick = {
                                onChange(DisplayMode.JAPANESE_NO_FURIGANA)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("한국어만") },
                            onClick = {
                                onChange(DisplayMode.KOREAN_ONLY)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
} 

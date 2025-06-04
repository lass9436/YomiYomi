package com.lass.yomiyomi.ui.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.ui.component.text.furigana.FuriganaText
import com.lass.yomiyomi.ui.component.text.tts.UnifiedTTSButton

@Composable
fun ParagraphSentenceCard(
    sentence: SentenceItem,
    displayMode: DisplayMode,
    showKorean: Boolean,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (onEdit != null || onDelete != null) 8.dp else 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (onEdit != null || onDelete != null) 2.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
                        size = 24.dp
                    )
                }
            }
            
            // 한국어
            if ((showKorean && displayMode != DisplayMode.JAPANESE_ONLY && displayMode != DisplayMode.JAPANESE_NO_FURIGANA) || displayMode == DisplayMode.KOREAN_ONLY) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sentence.korean,
                        fontSize = if (displayMode == DisplayMode.KOREAN_ONLY) 18.sp else 16.sp,
                        color = if (displayMode == DisplayMode.KOREAN_ONLY) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // 편집/삭제 버튼들
                    if (onEdit != null || onDelete != null) {
                        Row {
                            onEdit?.let {
                                IconButton(
                                    onClick = it,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "편집",
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                            
                            onDelete?.let {
                                IconButton(
                                    onClick = it,
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
            } else if (onEdit != null || onDelete != null) {
                // Korean이 안 보이는 모드일 때는 일본어 아래에 버튼들 배치
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    onEdit?.let {
                        IconButton(
                            onClick = it,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "편집",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    
                    onDelete?.let {
                        IconButton(
                            onClick = it,
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
} 
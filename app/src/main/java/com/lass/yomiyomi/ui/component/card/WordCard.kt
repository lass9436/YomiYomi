package com.lass.yomiyomi.ui.component.card

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.lass.yomiyomi.domain.model.entity.Item
import com.lass.yomiyomi.domain.model.entity.WordItem
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.ui.component.text.tts.WordTextWithAdaptiveTTS
import com.lass.yomiyomi.ui.component.text.tts.InfoRowWithTTS
import com.lass.yomiyomi.ui.theme.YomiYomiTheme

@Composable
fun WordCard(
    word: Item,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
    // Item 인터페이스를 통해 정보 가져오기
    val infoRows = word.toInfoRows()
    val mainText = word.getMainText()
    
    // 레벨과 품사 정보 찾기
    val level = infoRows.find { it.label.contains("레벨") }?.value ?: ""
    val type = infoRows.find { it.label.contains("품사") }?.value ?: ""
    
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Row 1: 레벨 배지, 메인 단어 + TTS, 편집/삭제 버튼(옵션)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 왼쪽: 레벨 배지
                if (level.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text(
                            text = level,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // 중앙: 단어 + TTS
                WordTextWithAdaptiveTTS(
                    text = mainText,
                    onTextClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://ja.dict.naver.com/#/search?range=word&query=${mainText}".toUri()
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
                
                // 오른쪽: 편집/삭제 버튼 (있을 때만)
                if (onEdit != null || onDelete != null) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        onEdit?.let { editCallback ->
                            IconButton(
                                onClick = editCallback,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "편집",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        onDelete?.let { deleteCallback ->
                            IconButton(
                                onClick = deleteCallback,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "삭제",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 나머지 정보들과 품사 표시
            infoRows.filter { !it.label.contains("레벨") && !it.label.contains("품사") }.forEach { infoRow ->
                if (infoRow.value.isNotBlank()) {
                    if (infoRow.isJapanese) {
                        InfoRowWithTTS(
                            label = infoRow.label,
                            value = infoRow.value
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${infoRow.label} ${infoRow.value}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // 품사 태그들 (마지막에)
            if (type.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        type.split(",").forEach { typeItem ->
                            val trimmedType = typeItem.trim()
                            if (trimmedType.isNotEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = trimmedType,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WordCardEditablePreview() {
    YomiYomiTheme {
        WordCard(
            word = MyWordItem(
                id = 1,
                word = "こんにちは",
                reading = "こんにちは",
                meaning = "안녕하세요",
                level = "N5",
                learningWeight = 0.9f,
                timestamp = System.currentTimeMillis(),
                type = "감탄사"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WordCardReadOnlyPreview() {
    YomiYomiTheme {
        WordCard(
            word = WordItem(
                id = 1,
                word = "勉強",
                reading = "べんきょう",
                meaning = "공부",
                level = "N4",
                learningWeight = 0.6f,
                timestamp = System.currentTimeMillis(),
                type = "명사"
            )
        )
    }
} 

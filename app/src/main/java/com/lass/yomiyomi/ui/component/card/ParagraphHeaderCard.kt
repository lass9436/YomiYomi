package com.lass.yomiyomi.ui.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.ui.component.text.tts.UnifiedTTSButton

@Composable
fun ParagraphHeaderCard(
    paragraph: ParagraphItem,
    sentenceCount: Int,
    sentences: List<SentenceItem> = emptyList(),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 제목과 전체 TTS 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = paragraph.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                
                // 전체 문단 TTS 버튼
                if (sentences.isNotEmpty()) {
                    val allJapaneseText = sentences
                        .sortedBy { it.orderInParagraph }
                        .joinToString("。") { it.japanese.replace(Regex("\\[.*?\\]"), "") }
                        .plus("。")
                    
                    UnifiedTTSButton(
                        text = allJapaneseText,
                        size = 28.dp
                    )
                }
            }
            
            if (paragraph.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = paragraph.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                AssistChip(
                    onClick = { },
                    label = { Text(paragraph.category, fontSize = 12.sp) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                AssistChip(
                    onClick = { },
                    label = { Text(paragraph.level.value ?: "ALL", fontSize = 12.sp) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                AssistChip(
                    onClick = { },
                    label = { Text("${sentenceCount}/${paragraph.totalSentences}문장", fontSize = 12.sp) }
                )
            }
        }
    }
} 
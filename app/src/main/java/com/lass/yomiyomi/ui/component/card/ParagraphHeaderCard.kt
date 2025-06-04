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
    // 🔥 문장들의 평균 학습 진도 계산
    val averageLearningProgress = if (sentences.isNotEmpty()) {
        sentences.map { it.learningProgress }.average().toFloat()
    } else {
        0f
    }
    
    // 🔥 학습 완료된 문장 수 계산 (learningProgress가 1.0f인 문장들)
    val completedSentenceCount = sentences.count { it.learningProgress >= 1.0f }
    val totalSentenceCount = sentences.size
    
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
                    label = { Text("${completedSentenceCount}/${totalSentenceCount}문장", fontSize = 12.sp) }
                )
            }
            
            // 🔥 학습 진도 표시 추가
            Spacer(modifier = Modifier.height(12.dp))
            Column {
                Text(
                    text = "학습 진도: ${(averageLearningProgress * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { averageLearningProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
} 
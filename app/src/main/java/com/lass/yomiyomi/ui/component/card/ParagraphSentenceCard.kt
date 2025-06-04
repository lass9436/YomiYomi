package com.lass.yomiyomi.ui.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(
                    text = sentence.korean,
                    fontSize = if (displayMode == DisplayMode.KOREAN_ONLY) 18.sp else 16.sp,
                    color = if (displayMode == DisplayMode.KOREAN_ONLY) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
} 
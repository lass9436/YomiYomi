package com.lass.yomiyomi.ui.component.list

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.lass.yomiyomi.domain.model.MyWordItem
import com.lass.yomiyomi.ui.component.speech.TextToSpeechButton
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.util.JapaneseTextFilter
import com.lass.yomiyomi.util.rememberSpeechManager
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun MyWordCard(
    myWord: MyWordItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val speechManager = rememberSpeechManager()
    val isSpeaking by speechManager.isSpeaking.collectAsState()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row 1: [N5] -------- [메인 단어] [TTS] -------- [수정/삭제]
            // 절대 중앙 정렬을 위해 Box로 전체 감싸기
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 왼쪽: 레벨 박스
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(
                        text = myWord.level,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // 절대 중앙: 메인 단어는 항상 완전 중앙, TTS는 정확한 위치에
                var textWidth by remember { mutableStateOf(0.dp) }
                val density = LocalDensity.current
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = myWord.word,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .onSizeChanged { size: IntSize ->
                                textWidth = with(density) { size.width.toDp() }
                            }
                            .clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://ja.dict.naver.com/#/search?range=word&query=${myWord.word}".toUri()
                                )
                                context.startActivity(intent)
                            }
                    )
                    
                    // 텍스트가 길면(4글자 이상) 아래에, 짧으면 옆에 배치
                    if (myWord.word.length >= 4) {
                        // 긴 텍스트: TTS를 바로 아래 중앙에
                        Spacer(modifier = Modifier.height(4.dp))
                        TextToSpeechButton(
                            text = myWord.word,
                            isSpeaking = isSpeaking,
                            onSpeak = { originalText ->
                                val japaneseText = JapaneseTextFilter.prepareTTSText(originalText)
                                if (japaneseText.isNotEmpty()) {
                                    speechManager.speakWithOriginalText(originalText, japaneseText)
                                }
                            },
                            onStop = { speechManager.stopSpeaking() },
                            size = 28.dp,
                            speechManager = speechManager
                        )
                    }
                }
                
                // 짧은 텍스트: TTS를 정확히 텍스트 끝점에서 15dp 떨어뜨림
                if (myWord.word.length < 4 && textWidth > 0.dp) {
                    TextToSpeechButton(
                        text = myWord.word,
                        isSpeaking = isSpeaking,
                        onSpeak = { originalText ->
                            val japaneseText = JapaneseTextFilter.prepareTTSText(originalText)
                            if (japaneseText.isNotEmpty()) {
                                speechManager.speakWithOriginalText(originalText, japaneseText)
                            }
                        },
                        onStop = { speechManager.stopSpeaking() },
                        size = 32.dp,
                        speechManager = speechManager,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = textWidth / 2 + 15.dp) // 텍스트 끝점에서 정확히 15dp
                    )
                }
                
                // 오른쪽: 편집/삭제 버튼
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "편집",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Row 2: 읽기: [읽기] [TTS]
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "읽기: ${myWord.reading}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                
                if (myWord.reading != myWord.word && myWord.reading.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TextToSpeechButton(
                        text = myWord.reading,
                        isSpeaking = isSpeaking,
                        onSpeak = { originalText ->
                            val japaneseText = JapaneseTextFilter.prepareTTSText(originalText)
                            if (japaneseText.isNotEmpty()) {
                                speechManager.speakWithOriginalText(originalText, japaneseText)
                            }
                        },
                        onStop = { speechManager.stopSpeaking() },
                        size = 28.dp,
                        speechManager = speechManager
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Row 3: 의미: [의미] ------------------------ [품사들을 작은 카드로]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "의미: ${myWord.meaning}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                
                // 품사를 쉼표로 split해서 작은 카드들로 표시
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    myWord.type.split(",").forEach { type ->
                        val trimmedType = type.trim()
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

@Preview(showBackground = true)
@Composable
fun MyWordCardPreview() {
    YomiYomiTheme {
        MyWordCard(
            myWord = MyWordItem(
                id = 1,
                word = "こんにちは",
                reading = "こんにちは",
                meaning = "안녕하세요",
                level = "N5",
                learningWeight = 0.9f,
                timestamp = System.currentTimeMillis(),
                type = "동사"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
} 

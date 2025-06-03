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
import com.lass.yomiyomi.domain.model.MyKanjiItem
import com.lass.yomiyomi.ui.component.speech.TextToSpeechButton
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.util.JapaneseTextFilter
import com.lass.yomiyomi.util.rememberSpeechManager
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun MyKanjiCard(
    myKanji: MyKanjiItem,
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row 1: [N5] -------- [메인 한자] [TTS] -------- [수정/삭제]
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
                        text = myKanji.level,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // 절대 중앙: 메인 한자는 항상 완전 중앙, TTS는 정확한 위치에
                var textWidth by remember { mutableStateOf(0.dp) }
                val density = LocalDensity.current
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = myKanji.kanji,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .onSizeChanged { size: IntSize ->
                                textWidth = with(density) { size.width.toDp() }
                            }
                            .clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://ja.dict.naver.com/#/search?range=word&query=${myKanji.kanji}".toUri()
                                )
                                context.startActivity(intent)
                            }
                    )
                    
                    // 텍스트가 길면(3글자 이상) 아래에, 짧으면 옆에 배치
                    if (myKanji.kanji.length >= 3) {
                        // 긴 텍스트: TTS를 바로 아래 중앙에
                        Spacer(modifier = Modifier.height(4.dp))
                        TextToSpeechButton(
                            text = myKanji.kanji,
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
                if (myKanji.kanji.length < 3 && textWidth > 0.dp) {
                    TextToSpeechButton(
                        text = myKanji.kanji,
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

            // Row 2: 음독 [TTS]
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "음독: ${myKanji.onyomi}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (myKanji.onyomi.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TextToSpeechButton(
                        text = myKanji.onyomi,
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

            Spacer(modifier = Modifier.height(4.dp))

            // Row 3: 훈독 [TTS]
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "훈독: ${myKanji.kunyomi}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (myKanji.kunyomi.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TextToSpeechButton(
                        text = myKanji.kunyomi,
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

            Spacer(modifier = Modifier.height(4.dp))

            // Row 4: 의미
            Text(
                text = "의미: ${myKanji.meaning}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyKanjiCardPreview() {
    YomiYomiTheme {
        MyKanjiCard(
            myKanji = MyKanjiItem(
                id = 1,
                kanji = "食",
                onyomi = "しょく",
                kunyomi = "た(べる)",
                meaning = "음식, 먹다",
                level = "N5",
                learningWeight = 0.8f,
                timestamp = System.currentTimeMillis()
            ),
            onEdit = {},
            onDelete = {}
        )
    }
} 

package com.lass.yomiyomi.ui.component.my

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
            // 상단: 한자와 레벨
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 한자와 TTS 버튼을 Box로 중앙 정렬
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // 한자 텍스트 - 정중앙
                    Text(
                        text = myKanji.kanji,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://ja.dict.naver.com/#/search?range=word&query=${myKanji.kanji}".toUri()
                            )
                            context.startActivity(intent)
                        }
                    )
                    
                    // 한자 발음 버튼 - 우측 절대 위치
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    ) {
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
                            speechManager = speechManager
                        )
                    }
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = myKanji.level,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 음독
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "음독: ${myKanji.onyomi}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                
                // 음독 발음 버튼 - 직접 TTS 처리
                if (myKanji.onyomi.isNotBlank()) {
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

            // 훈독
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "훈독: ${myKanji.kunyomi}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                
                // 훈독 발음 버튼 - 직접 TTS 처리
                if (myKanji.kunyomi.isNotBlank()) {
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

            // 의미
            Text(
                text = "의미: ${myKanji.meaning}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 하단: 편집/삭제 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "편집",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
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
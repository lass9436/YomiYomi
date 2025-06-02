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
import androidx.core.net.toUri
import com.lass.yomiyomi.domain.model.MyWordItem
import com.lass.yomiyomi.ui.component.speech.TextToSpeechButton
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.util.JapaneseTextFilter
import com.lass.yomiyomi.util.rememberSpeechManager

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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // 단어와 재생 버튼을 Box로 중앙 정렬
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // 단어 텍스트 - 정중앙
                    Text(
                        text = myWord.word,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://ja.dict.naver.com/#/search?range=word&query=${myWord.word}".toUri()
                            )
                            context.startActivity(intent)
                        }
                    )
                    
                    // 단어 발음 버튼 - 우측 절대 위치
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    ) {
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
                            speechManager = speechManager
                        )
                    }
                }
                
                // 읽기와 재생 버튼
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = myWord.reading,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // 읽기 발음 버튼 - 직접 TTS 처리
                    if (myWord.reading != myWord.word) {
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
                
                Text(
                    text = myWord.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // 레벨 정보
                Text(
                    text = "레벨: ${myWord.level}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            // 편집 및 삭제 버튼
            Column {
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

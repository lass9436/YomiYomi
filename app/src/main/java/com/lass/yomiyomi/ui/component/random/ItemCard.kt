package com.lass.yomiyomi.ui.component.random

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.data.model.MyKanji
import com.lass.yomiyomi.data.model.MyWord
import com.lass.yomiyomi.speech.SpeechManager
import com.lass.yomiyomi.ui.component.speech.TextToSpeechButton
import com.lass.yomiyomi.util.JapaneseTextFilter

@Composable
fun ItemCard(
    item: Any,
    onCardClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
    // TTS 기능 추가
    val speechManager = remember {
        SpeechManager(context)
    }
    val isSpeaking by speechManager.isSpeaking.collectAsState()
    
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 480.dp)
            .padding(8.dp)
            .then(
                if (onCardClick != null) {
                    Modifier.clickable(onClick = onCardClick)
                } else if (item is Kanji) {
                    Modifier.clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://ja.dict.naver.com/#/search?range=word&query=${item.kanji}".toUri()
                        )
                        context.startActivity(intent)
                    }
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (item) {
                is Kanji -> {
                    // 한자 표시와 TTS 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.kanji,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        TextToSpeechButton(
                            text = item.kanji,
                            isSpeaking = isSpeaking,
                            onSpeak = { 
                                val japaneseText = JapaneseTextFilter.prepareTTSText(it)
                                if (japaneseText.isNotEmpty()) {
                                    speechManager.speak(japaneseText)
                                }
                            },
                            onStop = { speechManager.stopSpeaking() },
                            size = 32.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRowWithTTS(
                        label = "음독 :", 
                        value = item.onyomi, 
                        speechManager = speechManager, 
                        isSpeaking = isSpeaking,
                        labelWidth = 60
                    )
                    InfoRowWithTTS(
                        label = "훈독 :", 
                        value = item.kunyomi, 
                        speechManager = speechManager, 
                        isSpeaking = isSpeaking,
                        labelWidth = 60
                    )
                    InfoRow(label = "의미 :", value = item.meaning, labelWidth = 60)
                    InfoRow(label = "레벨 :", value = item.level, labelWidth = 60)
                }
                is Word -> {
                    // 단어 표시와 TTS 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.word,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        TextToSpeechButton(
                            text = item.word,
                            isSpeaking = isSpeaking,
                            onSpeak = { 
                                val japaneseText = JapaneseTextFilter.prepareTTSText(it)
                                if (japaneseText.isNotEmpty()) {
                                    speechManager.speak(japaneseText)
                                }
                            },
                            onStop = { speechManager.stopSpeaking() },
                            size = 32.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRowWithTTS(
                        label = "읽기 :", 
                        value = item.reading, 
                        speechManager = speechManager, 
                        isSpeaking = isSpeaking,
                        labelWidth = 60
                    )
                    InfoRow(label = "품사 :", value = item.type, labelWidth = 60)
                    InfoRow(label = "의미 :", value = item.meaning, labelWidth = 60)
                    InfoRow(label = "레벨 :", value = item.level, labelWidth = 60)
                }
                is MyKanji -> {
                    // 내 한자 표시와 TTS 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.kanji,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        TextToSpeechButton(
                            text = item.kanji,
                            isSpeaking = isSpeaking,
                            onSpeak = { 
                                val japaneseText = JapaneseTextFilter.prepareTTSText(it)
                                if (japaneseText.isNotEmpty()) {
                                    speechManager.speak(japaneseText)
                                }
                            },
                            onStop = { speechManager.stopSpeaking() },
                            size = 32.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRowWithTTS(
                        label = "음독 :", 
                        value = item.onyomi, 
                        speechManager = speechManager, 
                        isSpeaking = isSpeaking,
                        labelWidth = 60
                    )
                    InfoRowWithTTS(
                        label = "훈독 :", 
                        value = item.kunyomi, 
                        speechManager = speechManager, 
                        isSpeaking = isSpeaking,
                        labelWidth = 60
                    )
                    InfoRow(label = "의미 :", value = item.meaning, labelWidth = 60)
                    InfoRow(label = "레벨 :", value = item.level, labelWidth = 60)
                }
                is MyWord -> {
                    // 내 단어 표시와 TTS 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.word,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        TextToSpeechButton(
                            text = item.word,
                            isSpeaking = isSpeaking,
                            onSpeak = { 
                                val japaneseText = JapaneseTextFilter.prepareTTSText(it)
                                if (japaneseText.isNotEmpty()) {
                                    speechManager.speak(japaneseText)
                                }
                            },
                            onStop = { speechManager.stopSpeaking() },
                            size = 32.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRowWithTTS(
                        label = "읽기 :", 
                        value = item.reading, 
                        speechManager = speechManager, 
                        isSpeaking = isSpeaking,
                        labelWidth = 60
                    )
                    InfoRow(label = "품사 :", value = item.type, labelWidth = 60)
                    InfoRow(label = "의미 :", value = item.meaning, labelWidth = 60)
                    InfoRow(label = "레벨 :", value = item.level, labelWidth = 60)
                }
            }
        }
    }
} 
package com.lass.yomiyomi.ui.component.random

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.lass.yomiyomi.domain.model.*
import com.lass.yomiyomi.ui.component.speech.TextToSpeechButton
import com.lass.yomiyomi.util.JapaneseTextFilter
import com.lass.yomiyomi.util.rememberSpeechManager

@Composable
fun ItemCard(
    item: Item,
    onCardClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
    // TTS 기능 - 직접 주입
    val speechManager = rememberSpeechManager()
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
            // 메인 텍스트와 TTS 버튼
            MainTextWithTTS(
                text = item.getMainText(),
                speechManager = speechManager,
                isSpeaking = isSpeaking,
                item = item,
                context = context
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 정보 행들 - 완전히 추상화됨
            InfoRows(
                infoRows = item.toInfoRows(),
                speechManager = speechManager,
                isSpeaking = isSpeaking
            )
        }
    }
}

@Composable
private fun MainTextWithTTS(
    text: String,
    speechManager: com.lass.yomiyomi.speech.SpeechManager,
    isSpeaking: Boolean,
    item: Item,
    context: android.content.Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.clickable {
                // 모든 아이템 타입에 대해 range=word로 통일
                val searchUrl = "https://ja.dict.naver.com/#/search?range=word&query=${text}"
                val intent = Intent(Intent.ACTION_VIEW, searchUrl.toUri())
                context.startActivity(intent)
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        TextToSpeechButton(
            text = text,
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

@Composable
private fun InfoRows(
    infoRows: List<InfoRowData>,
    speechManager: com.lass.yomiyomi.speech.SpeechManager,
    isSpeaking: Boolean
) {
    infoRows.forEach { infoRow ->
        if (infoRow.isJapanese) {
            InfoRowWithTTS(
                label = infoRow.label,
                value = infoRow.value,
                speechManager = speechManager,
                isSpeaking = isSpeaking,
                labelWidth = 60
            )
        } else {
            InfoRow(
                label = infoRow.label,
                value = infoRow.value,
                labelWidth = 60
            )
        }
    }
} 
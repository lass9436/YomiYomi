package com.lass.yomiyomi.ui.component.random

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.speech.SpeechManager
import com.lass.yomiyomi.ui.component.speech.TextToSpeechButton
import com.lass.yomiyomi.util.JapaneseTextFilter

@Composable
fun InfoRowWithTTS(
    label: String,
    value: String,
    speechManager: SpeechManager,
    isSpeaking: Boolean,
    labelWidth: Int = 50
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.width(labelWidth.dp)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }
        
        // 일본어 텍스트가 있는 경우에만 TTS 버튼 표시
        if (JapaneseTextFilter.containsJapanese(value)) {
            TextToSpeechButton(
                text = value,
                isSpeaking = isSpeaking,
                onSpeak = { 
                    val japaneseText = JapaneseTextFilter.prepareTTSText(it)
                    if (japaneseText.isNotEmpty()) {
                        speechManager.speak(japaneseText)
                    }
                },
                onStop = { speechManager.stopSpeaking() },
                size = 24.dp
            )
        }
    }
} 
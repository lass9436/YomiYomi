package com.lass.yomiyomi.ui.component.tts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.speech.SpeechManager

/**
 * 정보 행과 TTS 버튼을 함께 표시하는 컴포넌트
 * List의 읽기, Random의 InfoRow 등에서 사용
 */
@Composable
fun InfoRowWithTTS(
    label: String,
    value: String,
    speechManager: SpeechManager,
    modifier: Modifier = Modifier,
    labelWidth: Dp = 60.dp,
    showTTS: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label $value",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        if (showTTS && value.isNotBlank()) {
            Spacer(modifier = Modifier.width(8.dp))
            UnifiedTTSButton(
                text = value,
                speechManager = speechManager,
                size = 28.dp
            )
        }
    }
} 
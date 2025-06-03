package com.lass.yomiyomi.ui.component.tts

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.speech.SpeechManager
import com.lass.yomiyomi.util.JapaneseTextFilter

/**
 * 통일된 TTS 버튼 컴포넌트
 * 모든 UI 컴포넌트에서 공통으로 사용
 */
@Composable
fun UnifiedTTSButton(
    text: String,
    speechManager: SpeechManager,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    isEnabled: Boolean = true
) {
    val isSpeaking by speechManager.isSpeaking.collectAsState()
    val currentSpeakingText by speechManager.currentSpeakingText.collectAsState()
    
    val isThisTextSpeaking = isSpeaking && currentSpeakingText == text
    
    val rotation by animateFloatAsState(
        targetValue = if (isThisTextSpeaking) 360f else 0f,
        animationSpec = if (isThisTextSpeaking) {
            infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        } else {
            tween(200)
        },
        label = "rotation"
    )

    IconButton(
        onClick = {
            if (isThisTextSpeaking) {
                speechManager.stopSpeaking()
            } else {
                val japaneseText = JapaneseTextFilter.prepareTTSText(text)
                if (japaneseText.isNotEmpty()) {
                    speechManager.speakWithOriginalText(text, japaneseText)
                }
            }
        },
        enabled = isEnabled && text.isNotBlank(),
        modifier = modifier.size(size)
    ) {
        Icon(
            imageVector = if (isThisTextSpeaking) Icons.Default.Close else Icons.Default.PlayArrow,
            contentDescription = if (isThisTextSpeaking) "음성 중지" else "음성 재생",
            tint = if (isEnabled && text.isNotBlank()) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            },
            modifier = if (isThisTextSpeaking) {
                Modifier.graphicsLayer { rotationZ = rotation }
            } else {
                Modifier
            }
        )
    }
} 
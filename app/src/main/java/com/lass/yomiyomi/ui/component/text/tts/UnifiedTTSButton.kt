package com.lass.yomiyomi.ui.component.text.tts

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
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.speech.SpeechManager
import com.lass.yomiyomi.util.JapaneseTextFilter
import com.lass.yomiyomi.util.rememberSpeechManager

/**
 * 통일된 TTS 버튼 컴포넌트
 * 단일 텍스트와 문장 리스트 모두 지원하는 범용 TTS 버튼
 */
@Composable
fun UnifiedTTSButton(
    text: String = "",
    sentences: List<SentenceItem> = emptyList(),
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    isEnabled: Boolean = true,
    speechManager: SpeechManager? = null
) {
    // 🎯 speechManager 파라미터가 있으면 사용, 없으면 로컬 생성
    val finalSpeechManager = speechManager ?: rememberSpeechManager()
    
    // 🎯 입력 데이터 검증 및 텍스트 생성
    val finalText = when {
        text.isNotBlank() -> text
        sentences.isNotEmpty() -> sentences.joinToString("。") { it.japanese }
        else -> ""
    }
    
    if (finalText.isBlank()) return
    
    val isSpeaking by finalSpeechManager.isSpeaking.collectAsState()
    val currentSpeakingText by finalSpeechManager.currentSpeakingText.collectAsState()
    
    val isThisTextSpeaking = isSpeaking && currentSpeakingText == finalText
    
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
                finalSpeechManager.stopSpeaking()
            } else {
                val japaneseText = JapaneseTextFilter.prepareTTSText(finalText)
                if (japaneseText.isNotEmpty()) {
                    finalSpeechManager.speakWithOriginalText(finalText, japaneseText)
                }
            }
        },
        enabled = isEnabled && finalText.isNotBlank(),
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isThisTextSpeaking) Icons.Default.Close else Icons.Default.PlayArrow,
            contentDescription = if (isThisTextSpeaking) "음성 중지" else "음성 재생",
            tint = if (isEnabled && finalText.isNotBlank()) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            },
            modifier = Modifier
                .size(size * 0.6f) // 아이콘 크기를 size의 60%로 설정
                .then(
                    if (isThisTextSpeaking) {
                        Modifier.graphicsLayer { rotationZ = rotation }
                    } else {
                        Modifier
                    }
                )
        )
    }
} 
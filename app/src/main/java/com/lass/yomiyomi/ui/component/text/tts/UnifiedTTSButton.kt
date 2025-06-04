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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.speech.SpeechManager
import com.lass.yomiyomi.util.JapaneseTextFilter
import com.lass.yomiyomi.di.SpeechManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * 통일된 TTS 버튼 컴포넌트
 * 단일 텍스트와 문장 리스트 모두 지원하는 범용 TTS 버튼
 */
@Composable
fun UnifiedTTSButton(
    text: String = "",
    sentences: List<SentenceItem> = emptyList(),
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    isEnabled: Boolean = true,
    speechManager: SpeechManager? = null
) {
    val context = LocalContext.current
    
    // 🔥 speechManager 파라미터가 있으면 사용, 없으면 싱글톤에서 직접 가져오기
    // DisposableEffect 없이 순수하게 싱글톤만 사용하여 LazyColumn dispose 문제 해결
    val finalSpeechManager = speechManager ?: remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SpeechManagerEntryPoint::class.java
        ).speechManager()
    }
    
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
        modifier = modifier.size(size)
    ) {
        Icon(
            imageVector = if (isThisTextSpeaking) Icons.Default.Close else Icons.Default.PlayArrow,
            contentDescription = if (isThisTextSpeaking) "음성 중지" else "음성 재생",
            tint = if (isEnabled && finalText.isNotBlank()) {
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
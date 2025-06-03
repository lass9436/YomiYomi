package com.lass.yomiyomi.ui.component.tts

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * 메인 텍스트와 TTS 버튼을 함께 표시하는 컴포넌트
 * Quiz, Random 등에서 사용
 */
@Composable
fun MainTextWithTTS(
    text: String,
    speechManager: SpeechManager,
    fontSize: TextUnit = 48.sp,
    onTextClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // 메인 텍스트 - 정중앙
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = if (onTextClick != null) {
                Modifier.clickable { onTextClick() }
            } else {
                Modifier
            },
            lineHeight = fontSize * 1.125f
        )
        
        // TTS 버튼 - 우측 절대 위치
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            UnifiedTTSButton(
                text = text,
                speechManager = speechManager,
                size = 32.dp
            )
        }
    }
}

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

/**
 * 단어 카드의 복잡한 TTS 레이아웃을 위한 컴포넌트
 * 짧은 텍스트는 옆에, 긴 텍스트는 아래에 TTS 배치
 */
@Composable
fun WordTextWithAdaptiveTTS(
    text: String,
    speechManager: SpeechManager,
    onTextClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }
    
    if (text.length >= 4) {
        // 긴 텍스트: TTS를 아래 중앙에
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                modifier = if (onTextClick != null) {
                    Modifier.clickable { onTextClick() }
                } else {
                    Modifier
                }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            UnifiedTTSButton(
                text = text,
                speechManager = speechManager,
                size = 28.dp
            )
        }
    } else {
        // 짧은 텍스트: TTS를 텍스트 옆에
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .onSizeChanged { size ->
                        textWidth = with(density) { size.width.toDp() }
                    }
                    .then(
                        if (onTextClick != null) {
                            Modifier.clickable { onTextClick() }
                        } else {
                            Modifier
                        }
                    )
            )
            
            if (textWidth > 0.dp) {
                UnifiedTTSButton(
                    text = text,
                    speechManager = speechManager,
                    size = 32.dp,
                    modifier = Modifier.offset(x = textWidth / 2 + 15.dp)
                )
            }
        }
    }
}

/**
 * 한자 카드의 TTS 레이아웃을 위한 컴포넌트
 * 한자는 보통 1-2글자이므로 길이 기준을 2로 조정
 */
@Composable
fun KanjiTextWithAdaptiveTTS(
    text: String,
    speechManager: SpeechManager,
    onTextClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }
    
    if (text.length >= 2) {
        // 긴 텍스트(2글자 이상): TTS를 아래 중앙에
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = if (onTextClick != null) {
                    Modifier.clickable { onTextClick() }
                } else {
                    Modifier
                }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            UnifiedTTSButton(
                text = text,
                speechManager = speechManager,
                size = 28.dp
            )
        }
    } else {
        // 짧은 텍스트(1글자): TTS를 텍스트 옆에
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .onSizeChanged { size ->
                        textWidth = with(density) { size.width.toDp() }
                    }
                    .then(
                        if (onTextClick != null) {
                            Modifier.clickable { onTextClick() }
                        } else {
                            Modifier
                        }
                    )
            )
            
            if (textWidth > 0.dp) {
                UnifiedTTSButton(
                    text = text,
                    speechManager = speechManager,
                    size = 32.dp,
                    modifier = Modifier.offset(x = textWidth / 2 + 15.dp)
                )
            }
        }
    }
}

/**
 * Item 인터페이스를 활용한 통합 TTS 컴포넌트
 * Random에서 Item의 정보를 자동으로 TTS와 함께 표시
 */
@Composable
fun ItemInfoWithTTS(
    item: com.lass.yomiyomi.domain.model.Item,
    speechManager: SpeechManager,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        item.toInfoRows().forEach { infoRow ->
            if (infoRow.isJapanese) {
                InfoRowWithTTS(
                    label = infoRow.label,
                    value = infoRow.value,
                    speechManager = speechManager,
                    labelWidth = 60.dp
                )
            } else {
                InfoRowWithTTS(
                    label = infoRow.label,
                    value = infoRow.value,
                    speechManager = speechManager,
                    labelWidth = 60.dp,
                    showTTS = false // 일본어가 아닌 경우 TTS 숨김
                )
            }
        }
    }
} 
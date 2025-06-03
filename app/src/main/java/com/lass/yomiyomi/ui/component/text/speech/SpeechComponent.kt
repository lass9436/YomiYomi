package com.lass.yomiyomi.ui.component.text.speech

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.speech.SpeechManager
import kotlin.math.sin
import kotlin.math.cos

/**
 * 음성 인식 버튼 - 마이크 아이콘과 애니메이션
 */
@Composable
fun SpeechRecognitionButton(
    isListening: Boolean,
    isEnabled: Boolean = true,
    audioLevel: Float = 0f,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isListening) 1.1f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .size(80.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // 펄스 효과 (듣고 있을 때)
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
            )
        }
        
        // 메인 버튼
        FloatingActionButton(
            onClick = {
                if (isListening) {
                    onStopListening()
                } else {
                    onStartListening()
                }
            },
            containerColor = if (isListening) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (isListening) "음성 인식 중지" else "음성 인식 시작",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
        
        // 오디오 레벨 시각화
        if (isListening && audioLevel > 0) {
            AudioLevelVisualizer(
                audioLevel = audioLevel,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

/**
 * TTS 버튼 - 스피커 아이콘
 */
@Composable
fun TextToSpeechButton(
    text: String,
    isSpeaking: Boolean,
    isEnabled: Boolean = true,
    onSpeak: (String) -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    speechManager: SpeechManager? = null
) {
    // 현재 이 버튼의 텍스트가 재생 중인지 확인
    val isThisTextSpeaking = speechManager?.let { manager ->
        val currentText by manager.currentSpeakingText.collectAsState()
        isSpeaking && currentText == text
    } ?: isSpeaking
    
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
                onStop()
            } else {
                onSpeak(text)
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
 * 음성 인식 결과 표시 카드
 */
@Composable
fun SpeechRecognitionResult(
    recognizedText: String,
    partialText: String,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = recognizedText.isNotBlank() || isListening,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isListening) {
                    Text(
                        text = "듣고 있습니다...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (partialText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = partialText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (recognizedText.isNotBlank()) {
                    Text(
                        text = "인식된 텍스트:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = recognizedText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

/**
 * 오디오 레벨 시각화
 */
@Composable
private fun AudioLevelVisualizer(
    audioLevel: Float,
    modifier: Modifier = Modifier
) {
    val animatedLevel by animateFloatAsState(
        targetValue = audioLevel,
        animationSpec = tween(100),
        label = "audioLevel"
    )
    
    Canvas(modifier = modifier) {
        drawAudioWave(animatedLevel)
    }
}

private fun DrawScope.drawAudioWave(level: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = size.minDimension / 3
    
    // 원형 파형 그리기
    for (i in 0 until 360 step 10) {
        val angle = Math.toRadians(i.toDouble())
        val waveOffset = sin(angle * 4) * level * 10
        val startRadius = radius + waveOffset.toFloat()
        val endRadius = startRadius + 20
        
        drawLine(
            color = Color(0xFF2196F3).copy(alpha = 0.6f),
            start = Offset(
                (centerX + cos(angle) * startRadius).toFloat(),
                (centerY + sin(angle) * startRadius).toFloat()
            ),
            end = Offset(
                (centerX + cos(angle) * endRadius).toFloat(),
                (centerY + sin(angle) * endRadius).toFloat()
            ),
            strokeWidth = 4.dp.toPx()
        )
    }
}

/**
 * 음성 설정 컨트롤
 */
@Composable
fun SpeechSettingsPanel(
    speechRate: Float,
    pitch: Float,
    onSpeechRateChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "음성 설정",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 읽기 속도
            Text(
                text = "읽기 속도: ${String.format("%.1f", speechRate)}x",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = speechRate,
                onValueChange = onSpeechRateChange,
                valueRange = 0.1f..2.0f,
                steps = 18,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 음성 높이
            Text(
                text = "음성 높이: ${String.format("%.1f", pitch)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = pitch,
                onValueChange = onPitchChange,
                valueRange = 0.5f..2.0f,
                steps = 14,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

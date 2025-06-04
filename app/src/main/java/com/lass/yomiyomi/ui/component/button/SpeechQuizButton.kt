package com.lass.yomiyomi.ui.component.button

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SpeechQuizButton(
    isListening: Boolean,
    recognizedText: String,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onCheckAnswer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isListening) 360f else 0f,
        animationSpec = if (isListening) {
            infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        } else {
            tween(200)
        },
        label = "rotation"
    )

    val buttonColor by animateColorAsState(
        targetValue = if (isListening) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        label = "buttonColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // 음성 인식 버튼
        Button(
            onClick = {
                if (isListening) {
                    onStopListening()
                } else {
                    onStartListening()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            shape = CircleShape,
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer { rotationZ = rotation }
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Close else Icons.Default.PlayArrow,
                contentDescription = if (isListening) "음성 인식 중지" else "음성 인식 시작",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 인식된 텍스트 표시
        if (recognizedText.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "인식된 텍스트:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recognizedText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 정답 확인 버튼
                    Button(
                        onClick = { onCheckAnswer(recognizedText) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(
                            text = "정답 확인",
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else if (!isListening) {
            // 안내 텍스트
            Text(
                text = "🎙️ 버튼을 눌러 일본어로 답하세요",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        } else {
            // 듣는 중 안내
            Text(
                text = "🔊 듣고 있습니다...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
} 
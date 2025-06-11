package com.lass.yomiyomi.ui.component.button

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.speech.BackgroundTTSManager
import com.lass.yomiyomi.speech.BackgroundTTSSettings
import com.lass.yomiyomi.di.SpeechManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * 백그라운드 TTS 시작 버튼
 */
@Composable
fun BackgroundTTSButton(
    sentences: List<SentenceItem> = emptyList(),
    paragraphs: List<ParagraphItem> = emptyList(),
    sentencesMap: Map<Int, List<SentenceItem>> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backgroundTTSManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SpeechManagerEntryPoint::class.java
        ).backgroundTTSManager()
    }
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    val isPlaying by backgroundTTSManager.isPlaying.collectAsState()
    val isReady by backgroundTTSManager.isReady.collectAsState()
    val currentText by backgroundTTSManager.currentText.collectAsState()
    val progress by backgroundTTSManager.progress.collectAsState()
    val settings by backgroundTTSManager.settings.collectAsState()
    
    val rotation by animateFloatAsState(
        targetValue = if (isPlaying) 360f else 0f,
        animationSpec = if (isPlaying) {
            infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        } else {
            tween(200)
        },
        label = "rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 메인 버튼
        FilledTonalButton(
            onClick = {
                if (isPlaying) {
                    backgroundTTSManager.stop()
                } else {
                    if (sentences.isNotEmpty()) {
                        backgroundTTSManager.startSentenceLearning(sentences)
                    } else if (paragraphs.isNotEmpty()) {
                        backgroundTTSManager.startParagraphLearning(paragraphs, sentencesMap)
                    }
                }
            },
            enabled = isReady && (sentences.isNotEmpty() || paragraphs.isNotEmpty()),
            modifier = Modifier.height(56.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "백그라운드 학습 중지" else "백그라운드 학습 시작",
                modifier = Modifier
                    .size(24.dp)
                    .then(
                        if (isPlaying) {
                            Modifier.graphicsLayer { rotationZ = rotation }
                        } else {
                            Modifier
                        }
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isPlaying) "학습 중지" else "백그라운드 학습",
                fontSize = 16.sp
            )
        }
        
        // 설정 버튼
        TextButton(
            onClick = { showSettingsDialog = true },
            enabled = !isPlaying
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "설정",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("설정", fontSize = 12.sp)
        }
        
        // 진행상황 표시
        if (isPlaying && progress.totalCount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.progressPercent },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${progress.currentIndex + 1}/${progress.totalCount}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (currentText.isNotBlank()) {
                Text(
                    text = if (currentText.length > 30) "${currentText.take(30)}..." else currentText,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    // 설정 다이얼로그
    if (showSettingsDialog) {
        BackgroundTTSSettingsDialog(
            settings = settings,
            onSettingsChange = { newSettings ->
                backgroundTTSManager.updateSettings(newSettings)
            },
            onDismiss = { showSettingsDialog = false }
        )
    }
}

/**
 * 백그라운드 TTS 설정 다이얼로그
 */
@Composable
private fun BackgroundTTSSettingsDialog(
    settings: BackgroundTTSSettings,
    onSettingsChange: (BackgroundTTSSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSettings by remember { mutableStateOf(settings) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("백그라운드 학습 설정") },
        text = {
            Column {
                // 일본어 포함 설정
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = tempSettings.includeJapanese,
                        onCheckedChange = { 
                            tempSettings = tempSettings.copy(includeJapanese = it)
                        }
                    )
                    Text("일본어 읽기", modifier = Modifier.padding(start = 8.dp))
                }
                
                // 한국어 포함 설정
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = tempSettings.includeKorean,
                        onCheckedChange = { 
                            tempSettings = tempSettings.copy(includeKorean = it)
                        }
                    )
                    Text("한국어 읽기", modifier = Modifier.padding(start = 8.dp))
                }
                
                // 반복 설정
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = tempSettings.isRepeat,
                        onCheckedChange = { 
                            tempSettings = tempSettings.copy(isRepeat = it)
                        }
                    )
                    Text("반복 재생", modifier = Modifier.padding(start = 8.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 속도 설정
                Text("읽기 속도: ${(tempSettings.speechRate * 100).toInt()}%")
                Slider(
                    value = tempSettings.speechRate,
                    onValueChange = { 
                        tempSettings = tempSettings.copy(speechRate = it)
                    },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 피치 설정
                Text("음성 높이: ${(tempSettings.pitch * 100).toInt()}%")
                Slider(
                    value = tempSettings.pitch,
                    onValueChange = { 
                        tempSettings = tempSettings.copy(pitch = it)
                    },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSettingsChange(tempSettings)
                    onDismiss()
                }
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
} 
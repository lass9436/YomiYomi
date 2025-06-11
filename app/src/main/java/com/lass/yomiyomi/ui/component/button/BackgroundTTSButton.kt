package com.lass.yomiyomi.ui.component.button

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.media.BackgroundTTSSettings
import com.lass.yomiyomi.media.MediaManager
import com.lass.yomiyomi.di.MediaManagerEntryPoint
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
    val mediaManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            MediaManagerEntryPoint::class.java
        ).mediaManager()
    }
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    val isPlaying by mediaManager.backgroundTTSIsPlaying.collectAsState()
    val isReady by mediaManager.backgroundTTSIsReady.collectAsState()
    val currentText by mediaManager.backgroundTTSCurrentText.collectAsState()
    val progress by mediaManager.backgroundTTSProgress.collectAsState()
    val settings by mediaManager.backgroundTTSSettings.collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 메인 컨트롤 행 (한 줄로 배치)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 재생/정지 버튼과 텍스트
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        if (isPlaying) {
                            // 재생 중이면 정지
                            mediaManager.stopBackgroundTTS()
                        } else {
                            // 재생 중이 아니면 시작
                            if (sentences.isNotEmpty()) {
                                println("BackgroundTTS Debug: Starting sentence learning with "+sentences.size+" sentences")
                                mediaManager.startBackgroundSentenceLearning(sentences)
                            } else if (paragraphs.isNotEmpty()) {
                                println("BackgroundTTS Debug: Starting paragraph learning with "+paragraphs.size+" paragraphs, sentencesMap size: "+sentencesMap.size)
                                mediaManager.startBackgroundParagraphLearning(paragraphs, sentencesMap)
                            } else {
                                println("BackgroundTTS Debug: No sentences or paragraphs available")
                            }
                        }
                    },
                    enabled = isReady && (sentences.isNotEmpty() || paragraphs.isNotEmpty())
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "학습 중지" else "백그라운드 학습 시작",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = if (isPlaying) "학습 중..." else "백그라운드 학습",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // 설정 버튼과 텍스트
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showSettingsDialog = true },
                    enabled = !isPlaying
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "설정",
                        modifier = Modifier.size(20.dp),
                        tint = if (isPlaying) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                
                Text(
                    text = "설정",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPlaying) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
        
        // 진행상황 표시 (학습 중일 때만)
        if (isPlaying && progress.totalCount > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { progress.progressPercent },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${progress.currentIndex + 1}/${progress.totalCount}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (currentText.isNotBlank()) {
                    Text(
                        text = if (currentText.length > 25) "${currentText.take(25)}..." else currentText,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
    
    // 설정 다이얼로그
    if (showSettingsDialog) {
        BackgroundTTSSettingsDialog(
            settings = settings,
            onSettingsChange = { newSettings ->
                mediaManager.updateBackgroundTTSSettings(newSettings)
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

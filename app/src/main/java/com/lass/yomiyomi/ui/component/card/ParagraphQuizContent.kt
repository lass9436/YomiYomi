package com.lass.yomiyomi.ui.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.data.ParagraphQuiz
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.ui.component.button.ParagraphSpeechQuizButton
import com.lass.yomiyomi.ui.component.text.furigana.FuriganaText
import com.lass.yomiyomi.util.ParagraphQuizGenerator

@Composable
fun ParagraphQuizContent(
    isLoading: Boolean,
    quiz: ParagraphQuiz?,
    sentences: List<SentenceItem>,
    isListening: Boolean,
    recognizedText: String,
    isQuizCompleted: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onProcessRecognition: (String) -> List<String>,
    onResetQuiz: () -> Unit,
    insufficientDataMessage: String? = null,
    showKoreanTranslation: Boolean = true,
    onToggleKoreanTranslation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            insufficientDataMessage != null -> {
                Text(
                    text = insufficientDataMessage,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            quiz != null && sentences.isNotEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 제목
                    Text(
                        text = quiz.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    
                    // 진행률 표시
                    val progress = ParagraphQuizGenerator.getProgress(quiz)
                    Column {
                        Text(
                            text = "진행률: ${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    }
                    
                    // 한국어 번역 토글 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onToggleKoreanTranslation,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showKoreanTranslation) 
                                    MaterialTheme.colorScheme.secondary 
                                else 
                                    MaterialTheme.colorScheme.outline,
                                contentColor = if (showKoreanTranslation) 
                                    MaterialTheme.colorScheme.onSecondary 
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(
                                text = if (showKoreanTranslation) "한국어 숨기기 🙈" else "한국어 보기 🇰🇷",
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 일본어 텍스트 (문장별로 분리)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "일본어 📝",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 각 문장을 개별 Row로 표시
                            sentences.forEach { sentence ->
                                FuriganaText(
                                    text = sentence.japanese,
                                    displayMode = DisplayMode.FULL,
                                    fontSize = 18.sp,
                                    furiganaSize = 12.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    quiz = quiz
                                )
                            }
                        }
                    }
                    
                    // 한국어 번역
                    if (showKoreanTranslation) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "한국어 번역 🇰🇷",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // 각 문장의 한국어 번역을 개별 표시
                                sentences.forEach { sentence ->
                                    Text(
                                        text = sentence.korean,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    // 퀴즈 완료 메시지
                    if (isQuizCompleted) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🎉 모든 빈칸을 채웠습니다! 🎉",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "훌륭합니다! 다음 문단으로 이동하세요.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // 음성 인식 버튼 (퀴즈 미완료 시에만 표시)
                        ParagraphSpeechQuizButton(
                            isListening = isListening,
                            recognizedText = recognizedText,
                            onStartListening = onStartListening,
                            onStopListening = onStopListening,
                            onCheckAnswer = onProcessRecognition
                        )
                    }
                }
            }
            
            else -> {
                Text(
                    text = "문단을 불러올 수 없습니다.",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 
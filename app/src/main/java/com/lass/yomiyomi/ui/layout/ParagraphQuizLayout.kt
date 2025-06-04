package com.lass.yomiyomi.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.ui.component.button.LevelSelector
import com.lass.yomiyomi.ui.component.card.ParagraphQuizContent
import com.lass.yomiyomi.ui.state.ParagraphQuizState
import com.lass.yomiyomi.ui.state.ParagraphQuizCallbacks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphQuizLayout(
    title: String,
    state: ParagraphQuizState,
    callbacks: ParagraphQuizCallbacks,
    onBack: (() -> Unit)? = null,
    refreshButtonText: String = "새 퀴즈",
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        title, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    ) 
                },
                navigationIcon = {
                    onBack?.let {
                        IconButton(onClick = it) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "뒤로 가기",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Level Selector (availableLevels가 있을 때만 표시)
            if (state.availableLevels.isNotEmpty()) {
                LevelSelector(
                    selectedLevel = state.selectedLevel,
                    onLevelSelected = callbacks.onLevelSelected,
                    availableLevels = state.availableLevels
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quiz Content (문단 빈칸 채우기)
            ParagraphQuizContent(
                isLoading = state.isLoading,
                quiz = state.quiz,
                sentences = state.sentences,
                isListening = state.isListening,
                recognizedText = state.recognizedText,
                isQuizCompleted = state.isQuizCompleted,
                onStartListening = callbacks.onStartListening,
                onStopListening = callbacks.onStopListening,
                onProcessRecognition = callbacks.onProcessRecognition,
                onResetQuiz = callbacks.onResetQuiz,
                insufficientDataMessage = state.insufficientDataMessage,
                showKoreanTranslation = state.showKoreanTranslation,
                onToggleKoreanTranslation = callbacks.onToggleKoreanTranslation,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Refresh Button (새 퀴즈)
                Button(
                    onClick = callbacks.onRefresh,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = refreshButtonText,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Show Answers Button (정답 보기) - 퀴즈가 있고 완료되지 않았을 때만 표시
                if (state.quiz != null && !state.isQuizCompleted) {
                    Button(
                        onClick = callbacks.onShowAnswers,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "정답 보기",
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Reset Quiz Button (빈칸 리셋) - 퀴즈가 있고 완료되지 않았을 때만 표시
                if (state.quiz != null && !state.isQuizCompleted) {
                    Button(
                        onClick = callbacks.onResetQuiz,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "빈칸 리셋",
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
} 
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
import com.lass.yomiyomi.ui.component.button.LearningModeToggle
import com.lass.yomiyomi.ui.component.button.RefreshButton
import com.lass.yomiyomi.ui.component.dialog.output.QuizAnswerDialog
import com.lass.yomiyomi.ui.component.card.SentenceQuizContent
import com.lass.yomiyomi.ui.component.button.QuizTypeSelector
import com.lass.yomiyomi.ui.state.SentenceQuizState
import com.lass.yomiyomi.ui.state.SentenceQuizCallbacks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceQuizLayout(
    title: String,
    state: SentenceQuizState,
    callbacks: SentenceQuizCallbacks,
    onBack: (() -> Unit)? = null,
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
            // Level Selector - availableLevels가 비어있지 않을 때만 표시
            if (state.availableLevels.isNotEmpty()) {
                LevelSelector(
                    selectedLevel = state.selectedLevel,
                    onLevelSelected = callbacks.onLevelSelected,
                    availableLevels = state.availableLevels
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quiz Type Selector and Learning Mode Toggle in a Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuizTypeSelector(
                    quizTypes = state.quizTypes,
                    selectedQuizTypeIndex = state.selectedQuizTypeIndex,
                    onQuizTypeSelected = callbacks.onQuizTypeSelected,
                    modifier = Modifier.weight(0.7f)
                )

                LearningModeToggle(
                    isLearningMode = state.isLearningMode,
                    onLearningModeChanged = callbacks.onLearningModeChanged,
                    modifier = Modifier.weight(0.3f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quiz Content (음성 인식 버전)
            SentenceQuizContent(
                isLoading = state.isLoading,
                question = state.question,
                isListening = state.isListening,
                recognizedText = state.recognizedText,
                onStartListening = callbacks.onStartListening,
                onStopListening = callbacks.onStopListening,
                onCheckAnswer = callbacks.onCheckAnswer,
                insufficientDataMessage = state.insufficientDataMessage
            )

            Spacer(modifier = Modifier.weight(1f))

            // Refresh Button
            RefreshButton(
                onClick = callbacks.onRefresh,
                text = "새 퀴즈 가져오기"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Answer Dialog
            if (state.showDialog && state.answerResult != null) {
                QuizAnswerDialog(
                    answerResult = state.answerResult,
                    onDismiss = callbacks.onDismissDialog
                )
            }
        }
    }
} 
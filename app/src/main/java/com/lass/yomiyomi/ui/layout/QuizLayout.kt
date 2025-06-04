package com.lass.yomiyomi.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.component.button.LevelSelector
import com.lass.yomiyomi.ui.component.button.LearningModeToggle
import com.lass.yomiyomi.ui.component.button.RefreshButton
import com.lass.yomiyomi.ui.component.dialog.output.QuizAnswerDialog
import com.lass.yomiyomi.ui.component.card.QuizContent
import com.lass.yomiyomi.ui.component.button.QuizTypeSelector
import com.lass.yomiyomi.ui.state.QuizState
import com.lass.yomiyomi.ui.state.QuizCallbacks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizLayout(
    title: String,
    state: QuizState,
    callbacks: QuizCallbacks,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    extraContent: (@Composable () -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Level Selector
            LevelSelector(
                selectedLevel = state.selectedLevel,
                onLevelSelected = callbacks.onLevelSelected,
                availableLevels = state.availableLevels
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quiz Type Selector and Learning Mode Toggle in a Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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

            // Quiz Content
            QuizContent(
                isLoading = state.isLoading,
                question = state.question,
                options = state.options,
                onOptionSelected = callbacks.onOptionSelected,
                searchUrl = state.searchUrl,
                insufficientDataMessage = state.insufficientDataMessage
            )

            // Extra Content (for speech features)
            extraContent?.invoke()

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
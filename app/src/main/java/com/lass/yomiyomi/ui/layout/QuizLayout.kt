package com.lass.yomiyomi.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.ui.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizLayout(
    title: String,
    selectedLevel: Level,
    onLevelSelected: (Level) -> Unit,
    quizTypes: List<String>,
    selectedQuizTypeIndex: Int,
    onQuizTypeSelected: (Int) -> Unit,
    isLearningMode: Boolean,
    onLearningModeChanged: (Boolean) -> Unit,
    isLoading: Boolean,
    question: String?,
    options: List<String>,
    onOptionSelected: (Int) -> Unit,
    onRefresh: () -> Unit,
    showDialog: Boolean,
    answerResult: String?,
    onDismissDialog: () -> Unit,
    searchUrl: String,
    availableLevels: List<Level> = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.ALL)
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
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Level Selector
                LevelSelector(
                    selectedLevel = selectedLevel,
                    onLevelSelected = onLevelSelected,
                    availableLevels = availableLevels
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quiz Type Selector and Learning Mode Toggle in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuizTypeSelector(
                        quizTypes = quizTypes,
                        selectedQuizTypeIndex = selectedQuizTypeIndex,
                        onQuizTypeSelected = onQuizTypeSelected,
                        modifier = Modifier.weight(0.7f)
                    )
                    
                    LearningModeToggle(
                        isLearningMode = isLearningMode,
                        onLearningModeChanged = onLearningModeChanged,
                        modifier = Modifier.weight(0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quiz Content
                QuizContent(
                    isLoading = isLoading,
                    question = question,
                    options = options,
                    onOptionSelected = onOptionSelected,
                    searchUrl = searchUrl
                )

                Spacer(modifier = Modifier.weight(1f))

                // Refresh Button
                RefreshButton(
                    onClick = onRefresh
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Answer Dialog
                if (showDialog && answerResult != null) {
                    AlertDialog(
                        onDismissRequest = { },
                        confirmButton = {
                            TextButton(
                                onClick = onDismissDialog,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    "다음 문제",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp),
                        text = {
                            Text(
                                answerResult,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }
        }
    )
} 
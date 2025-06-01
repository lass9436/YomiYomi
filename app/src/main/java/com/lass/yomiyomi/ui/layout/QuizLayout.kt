package com.lass.yomiyomi.ui.layout

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.ui.component.LevelSelector

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

                // Quiz Type Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    quizTypes.forEachIndexed { index, type ->
                        Button(
                            onClick = { onQuizTypeSelected(index) },
                            colors = if (selectedQuizTypeIndex == index) {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary
                                )
                            } else {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        ) {
                            Text(type, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Learning Mode Toggle
                Button(
                    onClick = { onLearningModeChanged(!isLearningMode) },
                    colors = if (isLearningMode) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                ) {
                    Text("학습 모드", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quiz Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 500.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    } else if (question != null) {
                        QuizCard(
                            question = question,
                            options = options,
                            onOptionSelected = onOptionSelected,
                            searchUrl = searchUrl
                        )
                    } else {
                        Text(
                            text = "퀴즈 로드 실패",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Refresh Button
                Button(
                    onClick = onRefresh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("새 퀴즈 가져오기")
                }

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

@Composable
private fun QuizCard(
    question: String,
    options: List<String>,
    onOptionSelected: (Int) -> Unit,
    searchUrl: String
) {
    val context = LocalContext.current
    
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = question,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "$searchUrl${question.split(" / ")[0]}".toUri()
                        )
                        context.startActivity(intent)
                    },
                lineHeight = 36.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            options.forEachIndexed { index, option ->
                QuizOptionButton(
                    option = option,
                    onOptionSelected = { onOptionSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun QuizOptionButton(
    option: String,
    onOptionSelected: () -> Unit
) {
    Button(
        onClick = onOptionSelected,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        Text(option, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
} 
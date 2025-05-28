package com.lass.yomiyomi.ui.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.viewmodel.kanjiQuiz.DummyKanjiQuizViewModel
import com.lass.yomiyomi.viewmodel.kanjiQuiz.KanjiQuizViewModelInterface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiQuizScreen(
    kanjiQuizViewModel: KanjiQuizViewModelInterface,
    onBack: () -> Unit
) {
    val quizState = kanjiQuizViewModel.quizState.collectAsState()
    val isLoading = kanjiQuizViewModel.isLoading.collectAsState()
    var answerResult by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var levelSelected by remember { mutableStateOf(Level.ALL) }

    LaunchedEffect(levelSelected) {
        kanjiQuizViewModel.loadQuizByLevel(levelSelected)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "한자 퀴즈",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val levels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
                    levels.forEach { level ->
                        Button(
                            onClick = { levelSelected = level },
                            colors = if (levelSelected == level) {
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
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(50.dp, 30.dp),
                        ) {
                            Text(level.name)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 500.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                    } else if (quizState.value != null) {
                        KanjiQuizCard(
                            quizState.value!!,
                            onAnswerChecked = { isCorrect ->
                                if (isCorrect) {
                                    answerResult = "정답입니다!"
                                } else {
                                    val correct = quizState.value!!
                                    answerResult = "오답입니다!\n정답: ${correct.optionStrings[correct.correctIndex]}"
                                }
                                showDialog = true
                            }
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

                Button(
                    onClick = { kanjiQuizViewModel.loadQuizByLevel(levelSelected) },
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

                if (showDialog && answerResult != null) {
                    AlertDialog(
                        onDismissRequest = { },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    answerResult = null
                                    kanjiQuizViewModel.loadQuizByLevel(levelSelected)
                                },
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
                                answerResult!!,
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
fun KanjiQuizCard(
    quiz: KanjiQuiz,
    onAnswerChecked: (Boolean) -> Unit
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
                text = quiz.kanji,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://ja.dict.naver.com/#/search?range=word&query=${quiz.kanji}".toUri()
                        )
                        context.startActivity(intent)
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            quiz.optionStrings.forEachIndexed { index, option ->
                OptionButton(
                    option = option,
                    isCorrect = index == quiz.correctIndex,
                    onAnswerChecked = onAnswerChecked
                )
            }
        }
    }
}

@Composable
fun OptionButton(
    option: String,
    isCorrect: Boolean,
    onAnswerChecked: (Boolean) -> Unit
) {
    Button(
        onClick = { onAnswerChecked(isCorrect) },
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

@Preview(showBackground = true)
@Composable
fun KanjiQuizScreenPreview() {
    KanjiQuizScreen(
        kanjiQuizViewModel = DummyKanjiQuizViewModel(),
        onBack = {}
    )
}
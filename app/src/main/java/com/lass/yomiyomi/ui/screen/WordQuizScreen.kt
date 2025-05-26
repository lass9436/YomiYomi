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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.domain.model.WordQuiz
import com.lass.yomiyomi.domain.model.WordQuizType
import com.lass.yomiyomi.ui.theme.LimeAccent
import com.lass.yomiyomi.ui.theme.LimeGreen
import com.lass.yomiyomi.ui.theme.SoftLimeBackground
import com.lass.yomiyomi.viewmodel.wordQuiz.DummyWordQuizViewModel
import com.lass.yomiyomi.viewmodel.wordQuiz.WordQuizViewModelInterface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordQuizScreen(
    wordQuizViewModel: WordQuizViewModelInterface,
    onBack: () -> Unit
) {
    val quizState = wordQuizViewModel.quizState.collectAsState()
    val isLoading = wordQuizViewModel.isLoading.collectAsState()
    var answerResult by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var levelSelected by remember { mutableStateOf(Level.ALL) }
    var quizTypeSelected by remember { mutableStateOf(WordQuizType.WORD_TO_MEANING_READING) }

    // ViewModel의 loadQuiz를 최초 한 번 호출
    LaunchedEffect(levelSelected, quizTypeSelected) {
        wordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "단어 퀴즈",
                        color = LimeAccent,
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
                // 레벨 선택 버튼들
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 현재 N1 단어가 없어서 임시 주석 처리
                    // val levels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
                    val levels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.ALL)
                    levels.forEach { level ->
                        Button(
                            onClick = { levelSelected = level },
                            colors = if (levelSelected == level) {
                                ButtonDefaults.buttonColors(
                                    containerColor = LimeAccent,
                                    contentColor = Color.White
                                )
                            } else {
                                ButtonDefaults.buttonColors(
                                    containerColor = SoftLimeBackground,
                                    contentColor = LimeAccent
                                )
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(50.dp, 30.dp),
                        ) {
                            Text(level.name)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 퀴즈 타입 선택 버튼들
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { quizTypeSelected = WordQuizType.WORD_TO_MEANING_READING },
                        colors = if (quizTypeSelected == WordQuizType.WORD_TO_MEANING_READING) {
                            ButtonDefaults.buttonColors(
                                containerColor = LimeAccent,
                                contentColor = Color.White
                            )
                        } else {
                            ButtonDefaults.buttonColors(
                                containerColor = SoftLimeBackground,
                                contentColor = LimeAccent
                            )
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("단어→뜻", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { quizTypeSelected = WordQuizType.MEANING_READING_TO_WORD },
                        colors = if (quizTypeSelected == WordQuizType.MEANING_READING_TO_WORD) {
                            ButtonDefaults.buttonColors(
                                containerColor = LimeGreen,
                                contentColor = Color.White
                            )
                        } else {
                            ButtonDefaults.buttonColors(
                                containerColor = SoftLimeBackground,
                                contentColor = LimeAccent
                            )
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("뜻→단어", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 퀴즈 카드 및 Loading Indicator
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
                        WordQuizCard(
                            quizState.value!!,
                            onAnswerChecked = { isCorrect ->
                                if (isCorrect) {
                                    answerResult = "정답입니다!"
                                } else {
                                    val correct = quizState.value!!
                                    answerResult = "오답입니다!\n정답: ${correct.answer}"
                                }
                                showDialog = true
                            }
                        )
                    } else {
                        Text(text = "퀴즈 로드 실패", fontSize = 18.sp, color = Color.Red)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 새 퀴즈 가져오기 버튼
                Button(
                    onClick = { wordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeAccent,
                        contentColor = Color.White
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
                                    wordQuizViewModel.loadQuizByLevel(levelSelected, quizTypeSelected)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = LimeGreen
                                )
                            ) {
                                Text(
                                    "다음 문제", fontWeight = FontWeight.Bold,
                                    color = LimeAccent
                                )
                            }
                        },
                        containerColor = SoftLimeBackground,
                        shape = RoundedCornerShape(16.dp),
                        text = {
                            Text(
                                answerResult!!,
                                color = LimeAccent,
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
fun WordQuizCard(
    quiz: WordQuiz,
    onAnswerChecked: (Boolean) -> Unit
) {
    val context = LocalContext.current
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SoftLimeBackground
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
                text = quiz.question,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = LimeAccent,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        val searchQuery = quiz.question.split(" / ")[0] // '/' 앞의 첫 번째 부분만 검색
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://ja.dict.naver.com/#/search?range=word&query=${searchQuery}".toUri()
                        )
                        context.startActivity(intent)
                    },
                lineHeight = 36.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            quiz.options.forEachIndexed { index, option ->
                WordOptionButton(
                    option = option,
                    isCorrect = index == quiz.correctIndex,
                    onAnswerChecked = onAnswerChecked
                )
            }
        }
    }
}

@Composable
fun WordOptionButton(
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
            containerColor = LimeAccent,
            contentColor = Color.White
        )
    ) {
        Text(option, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun WordQuizScreenPreview() {
    WordQuizScreen(
        wordQuizViewModel = DummyWordQuizViewModel(),
        onBack = {}
    )
}
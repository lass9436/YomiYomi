package com.lass.yomiyomi.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.KanjiQuiz
import com.lass.yomiyomi.ui.theme.LimeAccent
import com.lass.yomiyomi.ui.theme.LimeGreenLight
import com.lass.yomiyomi.ui.theme.SoftLimeBackground
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

    // ViewModel의 loadQuiz를 최초 한 번 호출
    LaunchedEffect(Unit) {
        kanjiQuizViewModel.loadQuiz { it.onyomi } // 정답 속성을 음독으로 설정
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "한자 퀴즈",
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
                        KanjiQuizCard(quizState.value!!)
                    } else {
                        Text(text = "퀴즈 로드 실패", fontSize = 18.sp, color = Color.Red)
                    }
                }

                Spacer(modifier = Modifier.weight(1f)) // 남은 공간을 활용해 버튼을 아래로 배치

                // 랜덤 새 퀴즈 버튼
                Button(
                    onClick = { kanjiQuizViewModel.loadQuiz { it.kunyomi } }, // 훈독 속성을 정답으로 사용
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
                Spacer(modifier = Modifier.height(16.dp)) // 하단 여백 추가
            }
        }
    )
}

@Composable
fun KanjiQuizCard(quiz: KanjiQuiz) {
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
            // 퀴즈 질문(한자) 표시
            Text(
                text = quiz.kanji,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = LimeAccent,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 선택지 버튼 생성
            quiz.optionStrings.forEachIndexed { index, option ->
                OptionButton(option, isCorrect = index == quiz.correctIndex)
            }
        }
    }
}

@Composable
fun OptionButton(option: String, isCorrect: Boolean) {
    Button(
        onClick = {
            // 정답 여부 표시
            val message = if (isCorrect) "정답!" else "오답!"
            println(message) // 실제 앱에서는 Toast나 UI 변경 처리
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isCorrect) LimeGreenLight else LimeAccent,
            contentColor = Color.White
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
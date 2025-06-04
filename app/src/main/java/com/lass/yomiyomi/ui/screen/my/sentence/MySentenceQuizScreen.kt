package com.lass.yomiyomi.ui.screen.my.sentence

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.ui.component.card.ItemCard
import com.lass.yomiyomi.ui.component.button.LevelSelector
import com.lass.yomiyomi.viewmodel.mySentence.quiz.DummyMySentenceQuizViewModel
import com.lass.yomiyomi.viewmodel.mySentence.quiz.MySentenceQuizViewModel
import com.lass.yomiyomi.viewmodel.mySentence.quiz.MySentenceQuizViewModelInterface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySentenceQuizScreen(
    onBack: () -> Unit,
    mySentenceQuizViewModel: MySentenceQuizViewModelInterface = hiltViewModel<MySentenceQuizViewModel>()
) {
    // 백핸들러 등록
    BackHandler {
        onBack()
    }

    // ViewModel state 수집
    val isLoading by mySentenceQuizViewModel.isLoading.collectAsState()
    val selectedLevel by mySentenceQuizViewModel.selectedLevel.collectAsState()
    val quizState by mySentenceQuizViewModel.quizState.collectAsState()
    val availableLevels by mySentenceQuizViewModel.availableLevels.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "내 문장 퀴즈",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (quizState.currentQuestion != null && !quizState.isQuizFinished) {
                            mySentenceQuizViewModel.resetQuiz()
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
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
            if (quizState.currentQuestion == null && !quizState.isQuizFinished) {
                // 퀴즈 시작 전: 레벨 선택 UI
                LevelSelector(
                    selectedLevel = selectedLevel,
                    onLevelSelected = { mySentenceQuizViewModel.setSelectedLevel(it) },
                    availableLevels = availableLevels
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "문장 퀴즈 시작하기",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "선택한 레벨: ${selectedLevel.value ?: "전체"}",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { mySentenceQuizViewModel.startQuiz() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("퀴즈 시작", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            } else {
                // 퀴즈 진행 상태 표시
                if (quizState.currentQuestion != null || quizState.isQuizFinished) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "문제 ${quizState.currentQuestionIndex + 1}/${quizState.totalQuestions}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = "점수: ${quizState.score}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // 진행 바
                        LinearProgressIndicator(
                            progress = { if (quizState.totalQuestions > 0) (quizState.currentQuestionIndex + 1).toFloat() / quizState.totalQuestions else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 퀴즈 콘텐츠 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        }
                        quizState.isQuizFinished -> {
                            // 퀴즈 완료 화면
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "퀴즈 완료! 🎉",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Text(
                                        text = "최종 점수: ${quizState.score}/${quizState.totalQuestions}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    val percentage = if (quizState.totalQuestions > 0) 
                                        (quizState.score * 100) / quizState.totalQuestions else 0
                                    Text(
                                        text = "정답률: ${percentage}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { mySentenceQuizViewModel.resetQuiz() },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("다시 시작")
                                        }
                                        
                                        Button(
                                            onClick = onBack,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("완료")
                                        }
                                    }
                                }
                            }
                        }
                        quizState.currentQuestion != null -> {
                            // 퀴즈 진행 중
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // 문장 카드
                                ItemCard(item = quizState.currentQuestion!!)
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                if (!quizState.showAnswer) {
                                    // 답안 보기 버튼
                                    Text(
                                        text = "일본어를 한국어로 번역해보세요!",
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    Button(
                                        onClick = { mySentenceQuizViewModel.showAnswer() },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("정답 확인", style = MaterialTheme.typography.titleMedium)
                                    }
                                } else {
                                    // 정답 표시 및 평가 버튼들
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "정답",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            
                                            Text(
                                                text = quizState.currentQuestion!!.korean,
                                                style = MaterialTheme.typography.titleLarge,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    if (!quizState.isAnswered) {
                                        Text(
                                            text = "맞혔나요?",
                                            style = MaterialTheme.typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Button(
                                                onClick = { mySentenceQuizViewModel.answerCorrect() },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                )
                                            ) {
                                                Text("맞음 ✓")
                                            }
                                            
                                            OutlinedButton(
                                                onClick = { mySentenceQuizViewModel.answerIncorrect() },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("틀림 ✗")
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = { mySentenceQuizViewModel.nextQuestion() },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                if (quizState.currentQuestionIndex + 1 < quizState.totalQuestions) 
                                                    "다음 문제" else "퀴즈 완료",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            Text(
                                text = "퀴즈를 시작하려면 위의 '퀴즈 시작' 버튼을 눌러주세요!",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MySentenceQuizScreenPreview() {
    MySentenceQuizScreen(
        onBack = {},
        mySentenceQuizViewModel = DummyMySentenceQuizViewModel()
    )
} 

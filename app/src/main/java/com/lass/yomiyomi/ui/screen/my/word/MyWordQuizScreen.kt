package com.lass.yomiyomi.ui.screen.my.word

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.viewmodel.myWord.quiz.DummyMyWordQuizViewModel
import com.lass.yomiyomi.viewmodel.myWord.quiz.MyWordQuizViewModel
import com.lass.yomiyomi.viewmodel.myWord.quiz.MyWordQuizViewModelInterface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWordQuizScreen(
    onBack: () -> Unit,
    myWordQuizViewModel: MyWordQuizViewModelInterface = hiltViewModel<MyWordQuizViewModel>()
) {
    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    // 임시 UI - 추후 새로운 QuizLayout API로 마이그레이션 예정
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "내 단어 퀴즈 📚",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "내 단어 퀴즈",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "곧 업데이트 예정입니다! 🚧",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun MyWordQuizScreenPreview() {
    MyWordQuizScreen(
        onBack = {},
        myWordQuizViewModel = DummyMyWordQuizViewModel()
    )
} 

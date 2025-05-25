package com.lass.yomiyomi.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.ui.theme.LimeAccent
import com.lass.yomiyomi.ui.theme.LimeGreen
import com.lass.yomiyomi.ui.theme.SoftLimeBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToKanji: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToWordQuiz: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YomiYomi", color = LimeAccent) }, // 강조된 라임색 텍스트
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(SoftLimeBackground), // 화면 전체 배경색
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 첫 번째 버튼
                    Button(
                        onClick = onNavigateToKanji,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LimeAccent, // 라임색 버튼
                            contentColor = LimeGreen // 버튼 텍스트 색상
                        )
                    ) {
                        Text("랜덤 한자 카드 보기")
                    }

                    // 두 번째 버튼
                    Button(
                        onClick = onNavigateToQuiz,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LimeAccent,
                            contentColor = LimeGreen
                        )
                    ) {
                        Text("한자 퀴즈 시작")
                    }

                    // 세 번째 버튼 - 단어 퀴즈
                    Button(
                        onClick = onNavigateToWordQuiz,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LimeAccent,
                            contentColor = LimeGreen
                        )
                    ) {
                        Text("단어 퀴즈 시작")
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    MainMenuScreen(
        onNavigateToKanji = {},
        onNavigateToQuiz = {},
        onNavigateToWordQuiz = {}
    )
}
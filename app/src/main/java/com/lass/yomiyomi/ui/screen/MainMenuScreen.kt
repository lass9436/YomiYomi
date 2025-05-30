package com.lass.yomiyomi.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.ui.components.MenuCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToKanji: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToWordQuiz: () -> Unit,
    onNavigateToWordRandom: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "YomiYomi",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        MenuCard(
                            title = "한자 카드",
                            subtitle = "랜덤으로 한자를\n학습해보세요",
                            onClick = onNavigateToKanji
                        )
                    }
                    item {
                        MenuCard(
                            title = "한자 퀴즈",
                            subtitle = "한자 실력을\n테스트해보세요",
                            onClick = onNavigateToQuiz
                        )
                    }
                    item {
                        MenuCard(
                            title = "단어 카드",
                            subtitle = "랜덤으로 단어를\n학습해보세요",
                            onClick = onNavigateToWordRandom
                        )
                    }
                    item {
                        MenuCard(
                            title = "단어 퀴즈",
                            subtitle = "단어 실력을\n테스트해보세요",
                            onClick = onNavigateToWordQuiz
                        )
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
        onNavigateToWordQuiz = {},
        onNavigateToWordRandom = {}
    )
}
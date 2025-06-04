package com.lass.yomiyomi.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.ui.component.card.MenuCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    initialTabIndex: Int = 0,
    onNavigateToKanji: () -> Unit,
    onNavigateToKanjiList: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToWordQuiz: () -> Unit,
    onNavigateToWordRandom: () -> Unit,
    onNavigateToWordList: () -> Unit,
    onNavigateToMyWord: () -> Unit,
    onNavigateToMyKanji: () -> Unit,
    onNavigateToMyWordRandom: () -> Unit,
    onNavigateToMyKanjiRandom: () -> Unit,
    onNavigateToMyKanjiQuiz: () -> Unit,
    onNavigateToMyWordQuiz: () -> Unit,
    onNavigateToSentenceList: () -> Unit = {},
    onNavigateToSentenceRandom: () -> Unit = {},
    onNavigateToSentenceQuiz: () -> Unit = {},
    onNavigateToParagraphList: () -> Unit = {},
    onNavigateToParagraphRandom: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    val tabs = listOf("기초 학습", "단어 / 한자", "문장 / 문단")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "요미요미",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier.background(
                                if (selectedTabIndex == index) 
                                    MaterialTheme.colorScheme.primaryContainer
                                else 
                                    Color.Transparent
                            ),
                            text = { 
                                Text(
                                    title,
                                    color = if (selectedTabIndex == index) 
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    fontWeight = if (selectedTabIndex == index) 
                                        FontWeight.Bold 
                                    else 
                                        FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        },
        content = { paddingValues ->
            when (selectedTabIndex) {
                0 -> {
                    // 기초 학습 탭 (기존 "학습" 탭)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        item {
                            MenuCard(
                                title = "한자 목록",
                                subtitle = "모든 한자를\n둘러보세요",
                                onClick = onNavigateToKanjiList
                            )
                        }
                        item {
                            MenuCard(
                                title = "단어 목록",
                                subtitle = "모든 단어를\n둘러보세요",
                                onClick = onNavigateToWordList
                            )
                        }
                        item {
                            MenuCard(
                                title = "한자 카드",
                                subtitle = "랜덤으로 한자를\n학습해보세요",
                                onClick = onNavigateToKanji
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
                                title = "한자 퀴즈",
                                subtitle = "한자 실력을\n테스트해보세요",
                                onClick = onNavigateToQuiz
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
                1 -> {
                    // 단어 / 한자 탭 (기존 "내 학습" 탭)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        item {
                            MenuCard(
                                title = "내 한자",
                                subtitle = "나만의 한자장을\n만들어보세요",
                                onClick = onNavigateToMyKanji
                            )
                        }
                        item {
                            MenuCard(
                                title = "내 단어",
                                subtitle = "나만의 단어장을\n만들어보세요",
                                onClick = onNavigateToMyWord
                            )
                        }
                        item {
                            MenuCard(
                                title = "내 한자 랜덤",
                                subtitle = "랜덤으로 한자를\n학습해보세요",
                                onClick = onNavigateToMyKanjiRandom
                            )
                        }
                        item {
                            MenuCard(
                                title = "내 단어 랜덤",
                                subtitle = "랜덤으로 단어를\n학습해보세요",
                                onClick = onNavigateToMyWordRandom
                            )
                        }
                        item {
                            MenuCard(
                                title = "내 한자 퀴즈",
                                subtitle = "내 한자 실력을\n테스트해보세요",
                                onClick = onNavigateToMyKanjiQuiz
                            )
                        }
                        item {
                            MenuCard(
                                title = "내 단어 퀴즈",
                                subtitle = "내 단어 실력을\n테스트해보세요",
                                onClick = onNavigateToMyWordQuiz
                            )
                        }
                    }
                }
                2 -> {
                    // 문장 / 문단 탭 (새로 추가)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        item {
                            MenuCard(
                                title = "내 문장",
                                subtitle = "나만의 문장을\n학습해보세요",
                                onClick = onNavigateToSentenceList
                            )
                        }
                        item {
                            MenuCard(
                                title = "내 문단",
                                subtitle = "나만의 문단을\n연습해보세요",
                                onClick = onNavigateToParagraphList
                            )
                        }
                        item {
                            MenuCard(
                                title = "내 문장 랜덤",
                                subtitle = "랜덤으로 문장을\n학습해보세요",
                                onClick = onNavigateToSentenceRandom
                            )
                        }
                        item {
                            MenuCard(
                                title = "내 문단 랜덤",
                                subtitle = "랜덤으로 문단을\n학습해보세요",
                                onClick = onNavigateToParagraphRandom
                            )
                        }
                        item {
                            MenuCard(
                                title = "내 문장 퀴즈",
                                subtitle = "내 문장 실력을\n테스트해보세요",
                                onClick = onNavigateToSentenceQuiz
                            )
                        }
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
        initialTabIndex = 0,
        onNavigateToKanji = {},
        onNavigateToKanjiList = {},
        onNavigateToQuiz = {},
        onNavigateToWordQuiz = {},
        onNavigateToWordRandom = {},
        onNavigateToWordList = {},
        onNavigateToMyWord = {},
        onNavigateToMyKanji = {},
        onNavigateToMyWordRandom = {},
        onNavigateToMyKanjiRandom = {},
        onNavigateToMyKanjiQuiz = {},
        onNavigateToMyWordQuiz = {},
        onNavigateToSentenceList = {},
        onNavigateToSentenceRandom = {},
        onNavigateToSentenceQuiz = {},
        onNavigateToParagraphList = {},
        onNavigateToParagraphRandom = {}
    )
}

package com.lass.yomiyomi.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.ui.component.button.LevelSelector
import com.lass.yomiyomi.ui.component.button.RefreshButton
import com.lass.yomiyomi.ui.component.card.EmptyStateCard
import com.lass.yomiyomi.ui.component.card.ParagraphHeaderCard
import com.lass.yomiyomi.ui.component.card.ParagraphSentenceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphRandomLayout(
    title: String = "랜덤 문단 카드",
    selectedLevel: Level,
    paragraph: ParagraphItem?,
    sentences: List<SentenceItem>,
    isLoading: Boolean,
    displayMode: DisplayMode = DisplayMode.FULL,
    showKorean: Boolean = true,
    onLevelSelected: (Level) -> Unit,
    onRefresh: () -> Unit,
    onBack: (() -> Unit)? = null,
    availableLevels: List<Level> = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
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
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // 레벨 선택
                LevelSelector(
                    selectedLevel = selectedLevel,
                    onLevelSelected = onLevelSelected,
                    availableLevels = availableLevels
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 문단 상세 표시 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    } else if (paragraph != null) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 문단 정보 헤더
                            item {
                                ParagraphHeaderCard(
                                    paragraph = paragraph,
                                    sentenceCount = sentences.size
                                )
                            }
                            
                            // 문장 목록
                            if (sentences.isEmpty()) {
                                item {
                                    EmptyStateCard(text = "문장이 없습니다")
                                }
                            } else {
                                items(sentences) { sentence ->
                                    ParagraphSentenceCard(
                                        sentence = sentence,
                                        displayMode = displayMode,
                                        showKorean = showKorean
                                    )
                                }
                            }
                        }
                    } else {
                        EmptyStateCard(text = "문단이 없습니다.\n문단을 추가해주세요.")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 새로고침 버튼
                RefreshButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = "새로운 문단 가져오기"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
} 
package com.lass.yomiyomi.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.ui.component.button.LevelSelector
import com.lass.yomiyomi.ui.component.button.RefreshButton
import com.lass.yomiyomi.ui.component.text.furigana.FuriganaText
import com.lass.yomiyomi.ui.component.text.tts.UnifiedTTSButton

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
                                    ParagraphSentenceItem(
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

@Composable
private fun ParagraphHeaderCard(
    paragraph: ParagraphItem,
    sentenceCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = paragraph.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (paragraph.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = paragraph.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                AssistChip(
                    onClick = { },
                    label = { Text(paragraph.category, fontSize = 12.sp) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                AssistChip(
                    onClick = { },
                    label = { Text(paragraph.level.value ?: "ALL", fontSize = 12.sp) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                AssistChip(
                    onClick = { },
                    label = { Text("${sentenceCount}/${paragraph.totalSentences}문장", fontSize = 12.sp) }
                )
            }
        }
    }
}

@Composable
private fun ParagraphSentenceItem(
    sentence: SentenceItem,
    displayMode: DisplayMode,
    showKorean: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 일본어
            if (displayMode != DisplayMode.KOREAN_ONLY) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FuriganaText(
                        japaneseText = sentence.japanese,
                        displayMode = displayMode,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    
                    UnifiedTTSButton(
                        text = sentence.japanese,
                        size = 24.dp
                    )
                }
            }
            
            // 한국어
            if ((showKorean && displayMode != DisplayMode.JAPANESE_ONLY && displayMode != DisplayMode.JAPANESE_NO_FURIGANA) || displayMode == DisplayMode.KOREAN_ONLY) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = sentence.korean,
                    fontSize = if (displayMode == DisplayMode.KOREAN_ONLY) 18.sp else 16.sp,
                    color = if (displayMode == DisplayMode.KOREAN_ONLY) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
} 
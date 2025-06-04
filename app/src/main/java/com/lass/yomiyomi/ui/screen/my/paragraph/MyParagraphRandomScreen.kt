package com.lass.yomiyomi.ui.screen.my.paragraph

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.ui.component.text.furigana.FuriganaText
import com.lass.yomiyomi.ui.component.text.tts.UnifiedTTSButton
import com.lass.yomiyomi.ui.layout.ParagraphRandomLayout
import com.lass.yomiyomi.viewmodel.myParagraph.random.MyParagraphRandomViewModel
import com.lass.yomiyomi.viewmodel.myParagraph.random.MyParagraphRandomViewModelInterface
import com.lass.yomiyomi.viewmodel.myParagraph.random.DummyParagraphRandomViewModel

@Composable
fun MyParagraphRandomScreen(
    onBack: () -> Unit,
    onParagraphClick: ((String) -> Unit)? = null,
    paragraphViewModel: MyParagraphRandomViewModelInterface = hiltViewModel<MyParagraphRandomViewModel>()
) {
    val randomParagraph = paragraphViewModel.randomParagraph.collectAsState().value
    val sentences = paragraphViewModel.sentences.collectAsState().value
    val isLoading = paragraphViewModel.isLoading.collectAsState().value
    
    var selectedLevel by remember { mutableStateOf(Level.ALL) }
    var displayMode by remember { mutableStateOf(DisplayMode.FULL) }
    var showKorean by remember { mutableStateOf(true) }

    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    LaunchedEffect(selectedLevel) {
        paragraphViewModel.fetchRandomParagraphByLevel(selectedLevel.value)
    }

    ParagraphRandomLayout(
        title = "내 문단 랜덤",
        selectedLevel = selectedLevel,
        onLevelSelected = { selectedLevel = it },
        onRefresh = { paragraphViewModel.fetchRandomParagraphByLevel(selectedLevel.value) },
        onBack = onBack,
        availableLevels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else if (randomParagraph != null) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 문단 정보 헤더
                item {
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
                                text = randomParagraph.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (randomParagraph.description.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = randomParagraph.description,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                AssistChip(
                                    onClick = { },
                                    label = { Text(randomParagraph.category, fontSize = 12.sp) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                AssistChip(
                                    onClick = { },
                                    label = { Text(randomParagraph.level.value ?: "ALL", fontSize = 12.sp) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                AssistChip(
                                    onClick = { },
                                    label = { Text("${sentences.size}/${randomParagraph.totalSentences}문장", fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }
                
                // 문장 목록
                if (sentences.isEmpty()) {
                    item {
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
                                    text = "문장이 없습니다",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
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
            // 문단이 없는 경우
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
                        text = "문단이 없습니다.\n문단을 추가해주세요.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
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
fun MyParagraphRandomScreenPreview() {
    MyParagraphRandomScreen(
        onBack = {},
        onParagraphClick = null,
        paragraphViewModel = DummyParagraphRandomViewModel()
    )
} 
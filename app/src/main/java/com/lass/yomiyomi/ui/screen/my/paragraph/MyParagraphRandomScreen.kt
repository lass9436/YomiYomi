package com.lass.yomiyomi.ui.screen.my.paragraph

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.component.card.ItemCard
import com.lass.yomiyomi.ui.layout.RandomLayout
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.myParagraph.random.MyParagraphRandomViewModel
import com.lass.yomiyomi.viewmodel.myParagraph.random.MyParagraphRandomViewModelInterface
import com.lass.yomiyomi.viewmodel.myParagraph.random.DummyMyParagraphRandomViewModel

@Composable
fun MyParagraphRandomScreen(
    onBack: () -> Unit,
    myParagraphRandomViewModel: MyParagraphRandomViewModelInterface = hiltViewModel<MyParagraphRandomViewModel>()
) {
    val isLoading by myParagraphRandomViewModel.isLoading.collectAsState()
    val selectedLevel by myParagraphRandomViewModel.selectedLevel.collectAsState()
    val currentParagraph by myParagraphRandomViewModel.currentParagraph.collectAsState()
    val currentSentences by myParagraphRandomViewModel.currentSentences.collectAsState()
    val availableLevels by myParagraphRandomViewModel.availableLevels.collectAsState()

    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    // 레벨 변경시 랜덤 문단 새로 가져오기
    LaunchedEffect(selectedLevel) {
        myParagraphRandomViewModel.loadRandomParagraph()
    }

    // 초기 랜덤 문단 로드
    LaunchedEffect(Unit) {
        myParagraphRandomViewModel.loadRandomParagraph()
    }

    RandomLayout(
        title = "내 문단 랜덤",
        selectedLevel = selectedLevel,
        onLevelSelected = { myParagraphRandomViewModel.setSelectedLevel(it) },
        onRefresh = { myParagraphRandomViewModel.loadRandomParagraph() },
        onBack = onBack,
        availableLevels = availableLevels
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
            currentParagraph == null -> {
                Text(
                    text = if (selectedLevel == Level.ALL) 
                        "내 문단이 없습니다.\n문단 목록에서 문단을 추가해보세요!"
                    else 
                        "${selectedLevel.value} 레벨의 내 문단이 없습니다.",
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 문단 정보 카드
                    ItemCard(item = currentParagraph!!)
                    
                    if (currentSentences.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "문단 내 문장들 (${currentSentences.size}개)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(currentSentences) { sentence ->
                                ItemCard(item = sentence)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "이 문단에는 등록된 문장이 없습니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyParagraphRandomScreenPreview() {
    YomiYomiTheme {
        MyParagraphRandomScreen(
            onBack = {},
            myParagraphRandomViewModel = DummyMyParagraphRandomViewModel()
        )
    }
} 

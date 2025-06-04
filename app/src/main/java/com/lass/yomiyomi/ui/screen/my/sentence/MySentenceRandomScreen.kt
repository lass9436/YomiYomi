package com.lass.yomiyomi.ui.screen.my.sentence

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.component.card.ItemCard
import com.lass.yomiyomi.ui.layout.RandomLayout
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.mySentence.random.MySentenceRandomViewModel
import com.lass.yomiyomi.viewmodel.mySentence.random.MySentenceRandomViewModelInterface
import com.lass.yomiyomi.viewmodel.mySentence.random.DummyMySentenceRandomViewModel

@Composable
fun MySentenceRandomScreen(
    onBack: () -> Unit,
    mySentenceRandomViewModel: MySentenceRandomViewModelInterface = hiltViewModel<MySentenceRandomViewModel>()
) {
    val isLoading by mySentenceRandomViewModel.isLoading.collectAsState()
    val selectedLevel by mySentenceRandomViewModel.selectedLevel.collectAsState()
    val currentSentence by mySentenceRandomViewModel.currentSentence.collectAsState()
    val availableLevels by mySentenceRandomViewModel.availableLevels.collectAsState()

    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    // 레벨 변경시 랜덤 문장 새로 가져오기
    LaunchedEffect(selectedLevel) {
        mySentenceRandomViewModel.loadRandomSentence()
    }

    // 초기 랜덤 문장 로드
    LaunchedEffect(Unit) {
        mySentenceRandomViewModel.loadRandomSentence()
    }

    RandomLayout(
        title = "내 문장 랜덤",
        selectedLevel = selectedLevel,
        onLevelSelected = { mySentenceRandomViewModel.setSelectedLevel(it) },
        onRefresh = { mySentenceRandomViewModel.loadRandomSentence() },
        onBack = onBack,
        availableLevels = availableLevels
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
            currentSentence == null -> {
                Text(
                    text = if (selectedLevel == Level.ALL) 
                        "내 문장이 없습니다.\n문장 목록에서 문장을 추가해보세요!"
                    else 
                        "${selectedLevel.value} 레벨의 내 문장이 없습니다.",
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                ItemCard(item = currentSentence!!)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MySentenceRandomScreenPreview() {
    YomiYomiTheme {
        MySentenceRandomScreen(
            onBack = {},
            mySentenceRandomViewModel = DummyMySentenceRandomViewModel()
        )
    }
} 

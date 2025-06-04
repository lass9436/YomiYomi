package com.lass.yomiyomi.ui.screen.my.sentence

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.ui.component.card.SentenceCard
import com.lass.yomiyomi.ui.layout.SentenceRandomLayout
import com.lass.yomiyomi.viewmodel.mySentence.random.MySentenceRandomViewModel
import com.lass.yomiyomi.viewmodel.mySentence.random.MySentenceRandomViewModelInterface
import com.lass.yomiyomi.viewmodel.mySentence.random.DummySentenceRandomViewModel

@Composable
fun MySentenceRandomScreen(
    onBack: () -> Unit,
    sentenceViewModel: MySentenceRandomViewModelInterface = hiltViewModel<MySentenceRandomViewModel>()
) {
    val randomSentence = sentenceViewModel.randomSentence.collectAsState().value
    val isLoading = sentenceViewModel.isLoading.collectAsState().value
    
    var selectedLevel by remember { mutableStateOf(Level.ALL) }
    var selectedDisplayMode by remember { mutableStateOf(DisplayMode.FULL) }

    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    LaunchedEffect(selectedLevel) {
        sentenceViewModel.fetchRandomSentenceByLevel(selectedLevel.value)
    }

    SentenceRandomLayout(
        title = "내 문장 랜덤",
        selectedLevel = selectedLevel,
        selectedDisplayMode = selectedDisplayMode,
        onLevelSelected = { selectedLevel = it },
        onDisplayModeChanged = { selectedDisplayMode = it },
        onRefresh = { sentenceViewModel.fetchRandomSentenceByLevel(selectedLevel.value) },
        onBack = onBack,
        availableLevels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        } else if (randomSentence != null) {
            SentenceCard(
                sentence = randomSentence,
                displayMode = selectedDisplayMode,
                showKorean = selectedDisplayMode != DisplayMode.JAPANESE_ONLY && selectedDisplayMode != DisplayMode.JAPANESE_NO_FURIGANA,
                showProgress = false, // 랜덤 카드에서는 진도 표시 안함
                onEdit = null, // 랜덤 화면에서는 편집 불가
                onDelete = null, // 랜덤 화면에서는 삭제 불가
                onDisplayModeChange = null // 이미 레이아웃에서 관리
            )
        } else {
            // 문장이 없는 경우
            androidx.compose.material3.Text(
                text = "문장이 없습니다.\n문장을 추가해주세요.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun MySentenceRandomScreenPreview() {
    MySentenceRandomScreen(
        onBack = {},
        sentenceViewModel = DummySentenceRandomViewModel()
    )
} 
package com.lass.yomiyomi.ui.screen.my.sentence

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.DisplayMode
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
        sentence = randomSentence,
        isLoading = isLoading,
        onLevelSelected = { selectedLevel = it },
        onDisplayModeChanged = { selectedDisplayMode = it },
        onRefresh = { sentenceViewModel.fetchRandomSentenceByLevel(selectedLevel.value) },
        onBack = onBack,
        availableLevels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
    )
}

@Composable
fun MySentenceRandomScreenPreview() {
    MySentenceRandomScreen(
        onBack = {},
        sentenceViewModel = DummySentenceRandomViewModel()
    )
} 
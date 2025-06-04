package com.lass.yomiyomi.ui.screen.my.paragraph

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
        paragraph = randomParagraph,
        sentences = sentences,
        isLoading = isLoading,
        displayMode = displayMode,
        showKorean = showKorean,
        onLevelSelected = { selectedLevel = it },
        onRefresh = { paragraphViewModel.fetchRandomParagraphByLevel(selectedLevel.value) },
        onBack = onBack,
        availableLevels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
    )
}

@Composable
fun MyParagraphRandomScreenPreview() {
    MyParagraphRandomScreen(
        onBack = {},
        onParagraphClick = null,
        paragraphViewModel = DummyParagraphRandomViewModel()
    )
} 
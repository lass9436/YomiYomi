package com.lass.yomiyomi.ui.screen.basic.word

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
import com.lass.yomiyomi.ui.component.card.ItemCard
import com.lass.yomiyomi.ui.layout.RandomLayout
import com.lass.yomiyomi.viewmodel.word.random.DummyWordRandomViewModel
import com.lass.yomiyomi.viewmodel.word.random.WordRandomViewModel
import com.lass.yomiyomi.viewmodel.word.random.WordRandomViewModelInterface

@Composable
fun WordRandomScreen(
    onBack: () -> Unit,
    wordViewModel: WordRandomViewModelInterface = hiltViewModel<WordRandomViewModel>()
) {
    val randomWord = wordViewModel.randomWord.collectAsState().value
    var levelSelected by remember { mutableStateOf(Level.ALL) }

    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    LaunchedEffect(levelSelected) {
        wordViewModel.fetchRandomWordByLevel(levelSelected.value)
    }

    RandomLayout(
        title = "랜덤 단어 카드",
        selectedLevel = levelSelected,
        onLevelSelected = { levelSelected = it },
        onRefresh = { wordViewModel.fetchRandomWordByLevel(levelSelected.value) },
        onBack = onBack
    ) {
        if (randomWord != null) {
            ItemCard(item = randomWord)
        } else {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
fun WordRandomScreenPreview() {
    WordRandomScreen(
        onBack = {},
        wordViewModel = DummyWordRandomViewModel()
    )
}

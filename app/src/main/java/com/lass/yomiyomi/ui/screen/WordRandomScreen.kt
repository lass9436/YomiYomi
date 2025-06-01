package com.lass.yomiyomi.ui.screen

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
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.ui.component.ItemCard
import com.lass.yomiyomi.ui.layout.RandomLayout
import com.lass.yomiyomi.viewmodel.wordRandom.DummyWordRandomViewModel
import com.lass.yomiyomi.viewmodel.wordRandom.WordRandomViewModelInterface

@Composable
fun WordRandomScreen(
    wordViewModel: WordRandomViewModelInterface,
    onBack: () -> Unit
) {
    val randomWord = wordViewModel.randomWord.collectAsState().value
    var levelSelected by remember { mutableStateOf(Level.ALL) }

    LaunchedEffect(levelSelected) {
        wordViewModel.fetchRandomWordByLevel(levelSelected.value)
    }

    RandomLayout(
        title = "랜덤 단어 카드",
        selectedLevel = levelSelected,
        onLevelSelected = { levelSelected = it },
        onRefresh = { wordViewModel.fetchRandomWordByLevel(levelSelected.value) }
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
        wordViewModel = DummyWordRandomViewModel(),
        onBack = {}
    )
}

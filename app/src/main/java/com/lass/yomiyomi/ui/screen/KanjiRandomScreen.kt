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
import com.lass.yomiyomi.ui.component.RandomScreen
import com.lass.yomiyomi.viewmodel.kanjiRandom.DummyKanjiRandomRandomViewModel
import com.lass.yomiyomi.viewmodel.kanjiRandom.KanjiRandomViewModelInterface

@Composable
fun KanjiRandomScreen(
    kanjiViewModel: KanjiRandomViewModelInterface,
    onBack: () -> Unit
) {
    val randomKanji = kanjiViewModel.randomKanji.collectAsState().value
    var levelSelected by remember { mutableStateOf(Level.ALL) }

    LaunchedEffect(levelSelected) {
        kanjiViewModel.fetchRandomKanjiByLevel(levelSelected.value)
    }

    RandomScreen(
        title = "랜덤 한자 카드",
        selectedLevel = levelSelected,
        onLevelSelected = { levelSelected = it },
        onRefresh = { kanjiViewModel.fetchRandomKanjiByLevel(levelSelected.value) },
        availableLevels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
    ) {
        if (randomKanji != null) {
            ItemCard(item = randomKanji)
        } else {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
fun KanjiRandomScreenPreview() {
    KanjiRandomScreen(
        kanjiViewModel = DummyKanjiRandomRandomViewModel(),
        onBack = {}
    )
}
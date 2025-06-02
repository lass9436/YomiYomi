package com.lass.yomiyomi.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.MyWordItem
import com.lass.yomiyomi.ui.component.random.ItemCard
import com.lass.yomiyomi.ui.layout.RandomLayout
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.myWord.DummyMyWordViewModel
import com.lass.yomiyomi.viewmodel.myWord.MyWordViewModel
import com.lass.yomiyomi.viewmodel.myWord.MyWordViewModelInterface

@Composable
fun MyWordRandomScreen(
    onBack: () -> Unit,
    myWordViewModel: MyWordViewModelInterface = hiltViewModel<MyWordViewModel>()
) {
    val myWords by myWordViewModel.myWords.collectAsState()
    val isLoading by myWordViewModel.isLoading.collectAsState()
    var selectedLevel by remember { mutableStateOf(Level.ALL) }
    var currentRandomWord by remember { mutableStateOf<MyWordItem?>(null) }

    // 안드로이드 시스템 뒤로가기 버튼도 onBack과 같은 동작
    BackHandler { onBack() }

    // 레벨에 따라 필터링된 단어들
    val filteredWords = remember(myWords, selectedLevel) {
        if (selectedLevel == Level.ALL) {
            myWords
        } else {
            myWords.filter { it.level == selectedLevel.value }
        }
    }

    // 랜덤 단어 가져오기 함수
    fun getRandomWord() {
        if (filteredWords.isNotEmpty()) {
            currentRandomWord = filteredWords.random()
        } else {
            currentRandomWord = null
        }
    }

    // 레벨 변경시 랜덤 단어 새로 가져오기
    LaunchedEffect(selectedLevel, filteredWords) {
        getRandomWord()
    }

    RandomLayout(
        title = "내 단어 랜덤",
        selectedLevel = selectedLevel,
        onLevelSelected = { selectedLevel = it },
        onRefresh = { getRandomWord() },
        onBack = onBack,
        availableLevels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
            filteredWords.isEmpty() -> {
                Text(
                    text = if (selectedLevel == Level.ALL) 
                        "내 단어가 없습니다.\n+ 버튼을 눌러 단어를 추가해보세요!"
                    else 
                        "${selectedLevel.value} 레벨의 내 단어가 없습니다.",
                    textAlign = TextAlign.Center
                )
            }
            currentRandomWord != null -> {
                ItemCard(item = currentRandomWord!!)
            }
            else -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyWordRandomScreenPreview() {
    YomiYomiTheme {
        MyWordRandomScreen(
            onBack = {},
            myWordViewModel = DummyMyWordViewModel()
        )
    }
} 

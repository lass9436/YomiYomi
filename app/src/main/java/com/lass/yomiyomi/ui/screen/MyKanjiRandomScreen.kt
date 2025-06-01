package com.lass.yomiyomi.ui.screen

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
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.ui.component.random.ItemCard
import com.lass.yomiyomi.ui.layout.RandomLayout
import com.lass.yomiyomi.ui.theme.YomiYomiTheme
import com.lass.yomiyomi.viewmodel.myKanji.DummyMyKanjiViewModel
import com.lass.yomiyomi.viewmodel.myKanji.MyKanjiViewModel
import com.lass.yomiyomi.viewmodel.myKanji.MyKanjiViewModelInterface

@Composable
fun MyKanjiRandomScreen(
    onBack: () -> Unit,
    myKanjiViewModel: MyKanjiViewModelInterface = hiltViewModel<MyKanjiViewModel>()
) {
    val myKanji by myKanjiViewModel.myKanji.collectAsState()
    val isLoading by myKanjiViewModel.isLoading.collectAsState()
    var selectedLevel by remember { mutableStateOf(Level.ALL) }
    var currentRandomKanji by remember { mutableStateOf<com.lass.yomiyomi.data.model.MyKanji?>(null) }

    // 레벨에 따라 필터링된 한자들
    val filteredKanji = remember(myKanji, selectedLevel) {
        if (selectedLevel == Level.ALL) {
            myKanji
        } else {
            myKanji.filter { it.level == selectedLevel.value }
        }
    }

    // 랜덤 한자 가져오기 함수
    fun getRandomKanji() {
        if (filteredKanji.isNotEmpty()) {
            currentRandomKanji = filteredKanji.random()
        } else {
            currentRandomKanji = null
        }
    }

    // 레벨 변경시 랜덤 한자 새로 가져오기
    LaunchedEffect(selectedLevel, filteredKanji) {
        getRandomKanji()
    }

    RandomLayout(
        title = "내 한자 랜덤",
        selectedLevel = selectedLevel,
        onLevelSelected = { selectedLevel = it },
        onRefresh = { getRandomKanji() },
        onBack = onBack,
        availableLevels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
            filteredKanji.isEmpty() -> {
                Text(
                    text = if (selectedLevel == Level.ALL) 
                        "내 한자가 없습니다.\n+ 버튼을 눌러 한자를 추가해보세요!"
                    else 
                        "${selectedLevel.value} 레벨의 내 한자가 없습니다.",
                    textAlign = TextAlign.Center
                )
            }
            currentRandomKanji != null -> {
                ItemCard(item = currentRandomKanji!!)
            }
            else -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyKanjiRandomScreenPreview() {
    YomiYomiTheme {
        MyKanjiRandomScreen(
            onBack = {},
            myKanjiViewModel = DummyMyKanjiViewModel()
        )
    }
} 
package com.lass.yomiyomi.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.ui.theme.LimeAccent
import com.lass.yomiyomi.ui.theme.SoftLimeBackground
import com.lass.yomiyomi.viewmodel.kanjiRandom.DummyKanjiRandomRandomViewModel
import com.lass.yomiyomi.viewmodel.kanjiRandom.KanjiRandomViewModelInterface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiScreen(
    kanjiViewModel: KanjiRandomViewModelInterface,
    onBack: () -> Unit
) {
    val randomKanji = kanjiViewModel.randomKanji.collectAsState().value

    // ViewModel의 fetchRandomKanji를 최초 한 번 호출
    LaunchedEffect(Unit) {
        kanjiViewModel.fetchRandomKanji()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "랜덤 한자 카드",
                        color = LimeAccent,
                        fontWeight = FontWeight.Bold
                    )
                },
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // 랜덤 한자 카드 (높이 더 키움)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = 200.dp,
                            max = 500.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (randomKanji != null) {
                        KanjiCard(randomKanji)
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f)) // 남은 공간을 활용해 버튼을 아래로 밀어냄

                // 고정 위치의 랜덤 버튼
                Button(
                    onClick = { kanjiViewModel.fetchRandomKanji() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // 좌우 여백 추가
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeAccent,
                        contentColor = Color.White
                    )
                ) {
                    Text("랜덤 한자 가져오기")
                }
                Spacer(modifier = Modifier.height(16.dp)) // 바닥과의 여백
            }
        }
    )
}

@Composable
fun KanjiCard(kanji: Kanji) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SoftLimeBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 480.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 한자 (가운데 정렬)
            Text(
                text = kanji.kanji,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = LimeAccent,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 음독, 훈독, 뜻 표시
            InfoRow(label = "음독 :", value = kanji.onyomi)
            InfoRow(label = "훈독 :", value = kanji.kunyomi)
            InfoRow(label = "의미 :", value = kanji.meaning)
            InfoRow(label = "레벨 :", value = kanji.level)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.width(50.dp) // label이 고정된 크기만 차지
        )
        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth() // 남은 공간 활용
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KanjiScreenPreview() {
    KanjiScreen(
        kanjiViewModel = DummyKanjiRandomRandomViewModel(),
        onBack = {}
    )
}
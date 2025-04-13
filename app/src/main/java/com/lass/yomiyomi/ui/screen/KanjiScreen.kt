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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.viewmodel.KanjiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiScreen(
    kanjiViewModel: KanjiViewModel
) {
    val randomKanji = kanjiViewModel.randomKanji.collectAsState().value

    // ViewModel의 fetchRandomKanji를 최초 한 번 호출
    LaunchedEffect(Unit) {
        kanjiViewModel.fetchRandomKanji()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("랜덤 한자 카드") }
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
                            max = 500.dp),
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
                        .padding(horizontal = 16.dp) // 좌우 여백 추가
                ) {
                    Text("랜덤 한자 가져오기")
                }
                Spacer(modifier = Modifier.height(16.dp)) // 바닥과의 여백
            }
        }
    )
}

@Composable
fun KanjiCard(kanji: com.lass.yomiyomi.data.model.Kanji) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
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
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 음독, 훈독, 뜻 표시
            InfoRow(label = "음독 :", value = kanji.onyomi)
            InfoRow(label = "훈독 :", value = kanji.kunyomi)
            InfoRow(label = "뜻 :", value = kanji.meaning)
            InfoRow(label = "레벨 :", value = kanji.level)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.weight(2f)
        )
    }
}
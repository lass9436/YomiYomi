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
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 랜덤 한자가 존재할 때 표시, 없을 때 로딩 인디케이터
                if (randomKanji != null) {
                    KanjiCard(randomKanji) // 랜덤 한자 카드 표시
                    Spacer(modifier = Modifier.height(16.dp)) // 버튼 위의 여백 추가
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                // 랜덤 한자 가져오는 버튼
                Button(
                    onClick = { kanjiViewModel.fetchRandomKanji() },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("랜덤 한자 가져오기")
                }
            }
        }
    )
}

@Composable
fun KanjiCard(kanji: com.lass.yomiyomi.data.model.Kanji) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // 한자
            Text(
                text = kanji.kanji,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 음독
            InfoRow(label = "음독 :", value = kanji.onyomi)

            // 훈독
            InfoRow(label = "훈독 :", value = kanji.kunyomi)

            // 뜻
            InfoRow(label = "뜻 :", value = kanji.meaning)

            // 수준(Level)
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
package com.lass.yomiyomi.ui.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.net.toUri
import androidx.compose.ui.platform.LocalContext
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.ui.theme.LimeGreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiScreen(
    kanjiViewModel: KanjiRandomViewModelInterface,
    onBack: () -> Unit
) {
    val randomKanji = kanjiViewModel.randomKanji.collectAsState().value
    var levelSelected by remember { mutableStateOf(Level.ALL) }


    // ViewModel의 fetchRandomKanji를 최초 한 번 호출
    LaunchedEffect(levelSelected) {
        kanjiViewModel.fetchRandomKanjiByLevel(levelSelected.value)
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 사용 가능한 모든 레벨
                    val levels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
                    levels.forEach {
                            level ->
                        Button(
                            onClick = { levelSelected = level },
                            colors = if (levelSelected == level) {
                                // 선택된 버튼: 강조된 색상
                                ButtonDefaults.buttonColors(
                                    containerColor = LimeAccent, // 강조 색상
                                    contentColor = Color.White // 텍스트를 더 잘 보이게 흰색
                                )
                            } else {
                                // 선택되지 않은 버튼: 기본 색상
                                ButtonDefaults.buttonColors(
                                    containerColor = SoftLimeBackground, // 기본 배경색 (라임톤)
                                    contentColor = LimeAccent // 기본 텍스트 색상
                                )
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .size(50.dp, 30.dp),
                            ) {
                            Text(level.name)
                        }
                    }
                }
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
                    onClick = { kanjiViewModel.fetchRandomKanjiByLevel(levelSelected.value) },
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
    val context = LocalContext.current
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
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW,
                    "https://ja.dict.naver.com/#/search?range=word&query=${kanji.kanji}".toUri())
                context.startActivity(intent)
            },
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
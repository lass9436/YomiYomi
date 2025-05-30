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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lass.yomiyomi.data.model.Kanji
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.viewmodel.kanjiRandom.DummyKanjiRandomRandomViewModel
import com.lass.yomiyomi.viewmodel.kanjiRandom.KanjiRandomViewModelInterface
import androidx.core.net.toUri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiScreen(
    kanjiViewModel: KanjiRandomViewModelInterface,
    onBack: () -> Unit
) {
    val randomKanji = kanjiViewModel.randomKanji.collectAsState().value
    var levelSelected by remember { mutableStateOf(Level.ALL) }

    LaunchedEffect(levelSelected) {
        kanjiViewModel.fetchRandomKanjiByLevel(levelSelected.value)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "랜덤 한자 카드",
                        color = MaterialTheme.colorScheme.tertiary,
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
                    val levels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
                    levels.forEach { level ->
                        Button(
                            onClick = { levelSelected = level },
                            colors = if (levelSelected == level) {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary
                                )
                            } else {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(50.dp, 30.dp),
                        ) {
                            Text(level.name)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 500.dp),
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

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { kanjiViewModel.fetchRandomKanjiByLevel(levelSelected.value) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("랜덤 한자 가져오기")
                }
                Spacer(modifier = Modifier.height(16.dp))
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
            containerColor = MaterialTheme.colorScheme.background
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
                color = MaterialTheme.colorScheme.tertiary,
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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(50.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
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
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.Word
import com.lass.yomiyomi.viewmodel.wordRandom.DummyWordRandomViewModel
import com.lass.yomiyomi.viewmodel.wordRandom.WordRandomViewModelInterface

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "랜덤 단어 카드",
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
                    val levels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.ALL)
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
                    if (randomWord != null) {
                        WordCard(randomWord)
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { wordViewModel.fetchRandomWordByLevel(levelSelected.value) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("랜덤 단어 가져오기")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

@Composable
fun WordCard(word: Word) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = word.word,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRowWord(label = "읽기 :", value = word.reading)
            InfoRowWord(label = "품사 :", value = word.type)
            InfoRowWord(label = "의미 :", value = word.meaning)
            InfoRowWord(label = "레벨 :", value = word.level)
        }
    }
}

@Composable
fun InfoRowWord(label: String, value: String) {
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
fun WordRandomScreenPreview() {
    WordRandomScreen(
        wordViewModel = DummyWordRandomViewModel(),
        onBack = {}
    )
}

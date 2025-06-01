package com.lass.yomiyomi.ui.component.my

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.ui.component.common.LevelSelector
import com.lass.yomiyomi.viewmodel.myKanji.MyKanjiViewModelInterface

@Composable
fun AddKanjiDialog(
    viewModel: MyKanjiViewModelInterface,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("검색", "직접 입력")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "한자 추가",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                // 탭 선택
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { 
                                Text(
                                    title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                ) 
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> SearchKanjiTab(viewModel = viewModel)
                    1 -> DirectInputKanjiTab(viewModel = viewModel, onDismiss = onDismiss)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("닫기")
            }
        }
    )
}

@Composable
private fun SearchKanjiTab(viewModel: MyKanjiViewModelInterface) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                viewModel.searchOriginalKanji(it)
            },
            label = { Text("한자 검색") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (searchResults.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { kanji ->
                    SearchKanjiResultCard(
                        kanji = kanji,
                        onAdd = { viewModel.addKanjiToMyKanji(kanji) }
                    )
                }
            }
        } else if (searchQuery.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("검색 결과가 없습니다.")
            }
        }
    }
}

@Composable
private fun DirectInputKanjiTab(
    viewModel: MyKanjiViewModelInterface,
    onDismiss: () -> Unit
) {
    var kanji by remember { mutableStateOf("") }
    var onyomi by remember { mutableStateOf("") }
    var kunyomi by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf(Level.N5) }

    Column {
        OutlinedTextField(
            value = kanji,
            onValueChange = { kanji = it },
            label = { Text("한자") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = onyomi,
            onValueChange = { onyomi = it },
            label = { Text("음독") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = kunyomi,
            onValueChange = { kunyomi = it },
            label = { Text("훈독") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = meaning,
            onValueChange = { meaning = it },
            label = { Text("의미") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        LevelSelector(
            selectedLevel = selectedLevel,
            onLevelSelected = { selectedLevel = it },
            availableLevels = listOf(Level.N1, Level.N2, Level.N3, Level.N4, Level.N5)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (kanji.isNotBlank() && (onyomi.isNotBlank() || kunyomi.isNotBlank()) && meaning.isNotBlank()) {
                    viewModel.addMyKanjiDirectly(kanji, onyomi, kunyomi, meaning, selectedLevel)
                    onDismiss()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = kanji.isNotBlank() && (onyomi.isNotBlank() || kunyomi.isNotBlank()) && meaning.isNotBlank()
        ) {
            Text("추가")
        }
    }
} 
package com.lass.yomiyomi.ui.component.my

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lass.yomiyomi.data.model.Level
import com.lass.yomiyomi.data.model.MyKanji
import com.lass.yomiyomi.ui.component.common.LevelSelector
import com.lass.yomiyomi.viewmodel.myKanji.MyKanjiViewModelInterface

@Composable
fun EditKanjiDialog(
    myKanji: MyKanji,
    viewModel: MyKanjiViewModelInterface,
    onDismiss: () -> Unit
) {
    var kanji by remember { mutableStateOf(myKanji.kanji) }
    var onyomi by remember { mutableStateOf(myKanji.onyomi) }
    var kunyomi by remember { mutableStateOf(myKanji.kunyomi) }
    var meaning by remember { mutableStateOf(myKanji.meaning) }
    var selectedLevel by remember { 
        mutableStateOf(
            Level.values().find { it.value == myKanji.level } ?: Level.N5
        ) 
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "한자 수정",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (kanji.isNotBlank() && (onyomi.isNotBlank() || kunyomi.isNotBlank()) && meaning.isNotBlank()) {
                                val updatedKanji = myKanji.copy(
                                    kanji = kanji,
                                    onyomi = onyomi,
                                    kunyomi = kunyomi,
                                    meaning = meaning,
                                    level = selectedLevel.value ?: "N5"
                                )
                                viewModel.updateMyKanji(updatedKanji)
                                onDismiss()
                            }
                        },
                        enabled = kanji.isNotBlank() && (onyomi.isNotBlank() || kunyomi.isNotBlank()) && meaning.isNotBlank()
                    ) {
                        Text("수정")
                    }
                }
            }
        }
    }
} 
package com.lass.yomiyomi.ui.component.dialog.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.Level
import com.lass.yomiyomi.domain.model.MyKanjiItem
import com.lass.yomiyomi.ui.component.button.LevelSelector
import com.lass.yomiyomi.viewmodel.myKanji.MyKanjiViewModelInterface

@Composable
fun EditKanjiDialog(
    myKanji: MyKanjiItem,
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "한자 수정",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (kanji.isNotBlank() && (onyomi.isNotBlank() || kunyomi.isNotBlank()) && meaning.isNotBlank()) {
                        val updatedKanji = MyKanjiItem(
                            id = myKanji.id,
                            kanji = kanji,
                            onyomi = onyomi,
                            kunyomi = kunyomi,
                            meaning = meaning,
                            level = selectedLevel.value ?: "N5",
                            learningWeight = myKanji.learningWeight,
                            timestamp = myKanji.timestamp
                        )
                        viewModel.updateMyKanji(updatedKanji)
                        onDismiss()
                    }
                },
                enabled = kanji.isNotBlank() && (onyomi.isNotBlank() || kunyomi.isNotBlank()) && meaning.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(
                    "수정",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("취소")
            }
        }
    )
} 

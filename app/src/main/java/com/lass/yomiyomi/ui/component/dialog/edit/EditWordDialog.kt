package com.lass.yomiyomi.ui.component.dialog.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.MyWordItem
import com.lass.yomiyomi.viewmodel.myWord.MyWordViewModelInterface

@Composable
fun EditWordDialog(
    myWord: MyWordItem,
    viewModel: MyWordViewModelInterface,
    onDismiss: () -> Unit
) {
    var word by remember { mutableStateOf(myWord.word) }
    var reading by remember { mutableStateOf(myWord.reading) }
    var meaning by remember { mutableStateOf(myWord.meaning) }
    var type by remember { mutableStateOf(myWord.type) }
    var selectedLevel by remember { 
        mutableStateOf(
            Level.values().find { it.value == myWord.level } ?: Level.N5
        ) 
    }
    var errorMessage by remember { mutableStateOf("") }

    val levels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "단어 수정",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                OutlinedTextField(
                    value = word,
                    onValueChange = { 
                        word = it
                        errorMessage = ""
                    },
                    label = { Text("단어") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = reading,
                    onValueChange = { 
                        reading = it
                        errorMessage = ""
                    },
                    label = { Text("읽기") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = meaning,
                    onValueChange = { 
                        meaning = it
                        errorMessage = ""
                    },
                    label = { Text("의미") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = type,
                    onValueChange = { 
                        type = it
                        errorMessage = ""
                    },
                    label = { Text("품사") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "레벨 선택",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(levels) { level ->
                        FilterChip(
                            selected = selectedLevel == level,
                            onClick = { selectedLevel = level },
                            label = { Text(level.value ?: "") }
                        )
                    }
                }
                
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.updateMyWord(
                        myWord = myWord,
                        newWord = word,
                        newReading = reading,
                        newMeaning = meaning,
                        newType = type,
                        newLevel = selectedLevel.value ?: "N5"
                    ) { success, message ->
                        if (success) {
                            onDismiss()
                        } else {
                            errorMessage = message
                        }
                    }
                },
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

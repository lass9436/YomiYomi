package com.lass.yomiyomi.ui.component.dialog.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.ui.component.card.SearchResultCard
import com.lass.yomiyomi.viewmodel.myWord.MyWordViewModelInterface

@Composable
fun AddWordDialog(
    viewModel: MyWordViewModelInterface,
    onDismiss: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("검색", "직접 입력")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "단어 추가",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                TabRow(
                    selectedTabIndex = selectedTabIndex
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { 
                                Text(
                                    title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                ) 
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTabIndex) {
                    0 -> SearchWordContent(viewModel, onDismiss)
                    1 -> DirectInputContent(viewModel, onDismiss)
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
private fun SearchWordContent(
    viewModel: MyWordViewModelInterface,
    onDismiss: () -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var localSearchQuery by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = localSearchQuery,
            onValueChange = { 
                localSearchQuery = it
                viewModel.searchOriginalWords(it)
            },
            label = { Text("단어 검색") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("검색할 단어를 입력하세요") }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (isLoading) {
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
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { word ->
                    SearchResultCard(
                        word = word,
                        onAdd = { 
                            viewModel.addWordToMyWords(word) { success ->
                                if (success) {
                                    onDismiss()
                                }
                            }
                        }
                    )
                }
            }
        } else if (localSearchQuery.isNotBlank()) {
            Text("검색 결과가 없습니다.")
        }
    }
}

@Composable
private fun DirectInputContent(
    viewModel: MyWordViewModelInterface,
    onDismiss: () -> Unit
) {
    var word by remember { mutableStateOf("") }
    var reading by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf(Level.N5) }
    var errorMessage by remember { mutableStateOf("") }

    val levels = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1)

    Column {
        OutlinedTextField(
            value = word,
            onValueChange = { 
                word = it
                errorMessage = ""
            },
            label = { Text("단어") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("例: 食べる") }
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
            singleLine = true,
            placeholder = { Text("例: たべる") }
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
            singleLine = true,
            placeholder = { Text("例: 먹다") }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = type,
            onValueChange = { 
                type = it
                errorMessage = ""
            },
            label = { Text("품사 (선택사항)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("例: 동사, 명사, 형용사...") }
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
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextButton(
            onClick = {
                viewModel.addMyWordDirectly(
                    word = word,
                    reading = reading,
                    meaning = meaning,
                    type = type.ifBlank { "명사" },
                    level = selectedLevel.value ?: "N5"
                ) { success, message ->
                    if (success) {
                        onDismiss()
                    } else {
                        errorMessage = message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(
                "추가",
                fontWeight = FontWeight.Bold
            )
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
} 

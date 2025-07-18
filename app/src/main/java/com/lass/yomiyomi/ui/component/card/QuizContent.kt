package com.lass.yomiyomi.ui.component.card

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizContent(
    isLoading: Boolean,
    question: String?,
    options: List<String>,
    onOptionSelected: (Int) -> Unit,
    searchUrl: String,
    insufficientDataMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 500.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
            insufficientDataMessage != null -> {
                Text(
                    text = insufficientDataMessage,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            question != null -> {
                QuizCard(
                    question = question,
                    options = options,
                    onOptionSelected = onOptionSelected,
                    searchUrl = searchUrl
                )
            }
            else -> {
                Text(
                    text = "퀴즈 로드 실패",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
} 
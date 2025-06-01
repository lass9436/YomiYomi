package com.lass.yomiyomi.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizTypeSelector(
    quizTypes: List<String>,
    selectedQuizTypeIndex: Int,
    onQuizTypeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        quizTypes.forEachIndexed { index, type ->
            Button(
                onClick = { onQuizTypeSelected(index) },
                colors = if (selectedQuizTypeIndex == index) {
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
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(type, fontSize = 12.sp)
            }
        }
    }
} 
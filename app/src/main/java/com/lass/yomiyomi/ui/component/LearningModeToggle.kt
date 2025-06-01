package com.lass.yomiyomi.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LearningModeToggle(
    isLearningMode: Boolean,
    onLearningModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onLearningModeChanged(!isLearningMode) },
        colors = if (isLearningMode) {
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
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Text("학습 모드", fontSize = 12.sp)
    }
} 
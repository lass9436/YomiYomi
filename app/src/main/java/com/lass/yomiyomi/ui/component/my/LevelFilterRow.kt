package com.lass.yomiyomi.ui.component.my

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun LevelFilterRow(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    val levels = listOf("ALL", "N1", "N2", "N3", "N4", "N5")
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(levels) { level ->
            FilterChip(
                selected = selectedLevel == level,
                onClick = { onLevelSelected(level) },
                label = { Text(level) }
            )
        }
    }
} 
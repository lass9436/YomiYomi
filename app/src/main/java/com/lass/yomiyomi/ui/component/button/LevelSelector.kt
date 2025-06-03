package com.lass.yomiyomi.ui.component.button

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.Level

@Composable
fun LevelSelector(
    selectedLevel: Level,
    onLevelSelected: (Level) -> Unit,
    availableLevels: List<Level> = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        availableLevels.forEach { level ->
            Button(
                onClick = { onLevelSelected(level) },
                colors = if (selectedLevel == level) {
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
} 

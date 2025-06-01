package com.lass.yomiyomi.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.data.model.Level

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomScreen(
    title: String,
    selectedLevel: Level,
    onLevelSelected: (Level) -> Unit,
    onRefresh: () -> Unit,
    availableLevels: List<Level> = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.ALL),
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                LevelSelector(
                    selectedLevel = selectedLevel,
                    onLevelSelected = onLevelSelected,
                    availableLevels = availableLevels
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 500.dp),
                    contentAlignment = Alignment.Center
                ) {
                    content()
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onRefresh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("랜덤 가져오기")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
} 
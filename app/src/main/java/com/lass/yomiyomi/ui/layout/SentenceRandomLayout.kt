package com.lass.yomiyomi.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.constant.DisplayMode
import com.lass.yomiyomi.ui.component.button.LevelSelector
import com.lass.yomiyomi.ui.component.button.RefreshButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceRandomLayout(
    title: String = "랜덤 문장 카드",
    selectedLevel: Level,
    selectedDisplayMode: DisplayMode,
    onLevelSelected: (Level) -> Unit,
    onDisplayModeChanged: (DisplayMode) -> Unit,
    onRefresh: () -> Unit,
    onBack: (() -> Unit)? = null,
    availableLevels: List<Level> = listOf(Level.N5, Level.N4, Level.N3, Level.N2, Level.N1, Level.ALL),
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
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // 레벨 선택
                LevelSelector(
                    selectedLevel = selectedLevel,
                    onLevelSelected = onLevelSelected,
                    availableLevels = availableLevels
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 표시 모드 선택
                DisplayModeSelector(
                    selectedDisplayMode = selectedDisplayMode,
                    onDisplayModeChanged = onDisplayModeChanged
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 문장 카드 표시 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    content()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 새로고침 버튼
                RefreshButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = "새로운 문장 가져오기"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayModeSelector(
    selectedDisplayMode: DisplayMode,
    onDisplayModeChanged: (DisplayMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Text(
            text = "표시 모드",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = getDisplayModeText(selectedDisplayMode),
                onValueChange = { /* 읽기 전용 */ },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DisplayMode.values().forEach { mode ->
                    DropdownMenuItem(
                        text = { Text(getDisplayModeText(mode)) },
                        onClick = {
                            onDisplayModeChanged(mode)
                            expanded = false
                        },
                        leadingIcon = if (mode == selectedDisplayMode) {
                            {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

private fun getDisplayModeText(displayMode: DisplayMode): String {
    return when (displayMode) {
        DisplayMode.FULL -> "전체 표시"
        DisplayMode.JAPANESE_ONLY -> "일본어만"
        DisplayMode.JAPANESE_NO_FURIGANA -> "요미가나 없이"
        DisplayMode.KOREAN_ONLY -> "한국어만"
    }
} 
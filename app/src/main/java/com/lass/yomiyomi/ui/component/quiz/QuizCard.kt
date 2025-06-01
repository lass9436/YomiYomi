package com.lass.yomiyomi.ui.component.quiz

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@Composable
fun QuizCard(
    question: String,
    options: List<String>,
    onOptionSelected: (Int) -> Unit,
    searchUrl: String
) {
    val context = LocalContext.current
    
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = question,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "$searchUrl${question.split(" / ")[0]}".toUri()
                        )
                        context.startActivity(intent)
                    },
                lineHeight = 36.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            options.forEachIndexed { index, option ->
                QuizOptionButton(
                    option = option,
                    onOptionSelected = { onOptionSelected(index) }
                )
            }
        }
    }
} 
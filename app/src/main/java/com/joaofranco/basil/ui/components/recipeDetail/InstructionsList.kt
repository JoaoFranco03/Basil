package com.joaofranco.basil.ui.components.recipeDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InstructionsList(instructions: List<String>) {
    HorizontalDivider(
        modifier = Modifier.padding(top =12.dp),
        thickness = 1.dp,
        color = Color.Gray.copy(alpha = 0.5f)
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Instructions",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
        )
        instructions.forEachIndexed { index, instruction ->
            InstructionItem(index, instruction)
            if (index < instructions.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun InstructionItem(index: Int, instruction: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = "STEP ${index + 1}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = instruction,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
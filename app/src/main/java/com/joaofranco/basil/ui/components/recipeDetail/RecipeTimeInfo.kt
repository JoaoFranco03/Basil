package com.joaofranco.basil.ui.components.recipeDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joaofranco.basil.data.model.Recipe

@Composable
fun RecipeTimeInfo(recipe: Recipe) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set a larger weight for the Prep and Total time columns
        TimeColumn(label = "Prep. Time", time = recipe.prepTime)
        DividerLine()
        // Set a smaller weight for the Cooking Time column
        TimeColumn(label = "Cooking Time", time = recipe.cookTime)
        DividerLine()
        TimeColumn(label = "Total Time", time = recipe.totalTime)
    }
}

@Composable
fun TimeColumn(label: String, time: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            "$time min",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DividerLine() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(Color.Gray.copy(alpha = 0.5f))
    )
}
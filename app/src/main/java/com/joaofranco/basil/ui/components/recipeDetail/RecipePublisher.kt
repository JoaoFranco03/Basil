package com.joaofranco.basil.ui.components.recipeDetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joaofranco.basil.utils.LinkToPublisherName

@Composable
fun RecipePublisher(recipeUrl: String, context: Context) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val publisherName = LinkToPublisherName(recipeUrl).publisherName
        TextButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipeUrl))
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Recipe by $publisherName",
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
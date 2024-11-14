package com.joaofranco.basil.ui.components.recipeDetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joaofranco.basil.R
import com.joaofranco.basil.data.model.Recipe

@Composable
fun RecipeHeader(recipe: Recipe) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Title and Description Section
        Column(
            modifier = Modifier
                .weight(1f) // Ensures Column takes up only necessary space and allows IconButton room
                .padding(end = 8.dp) // Space between text and icon
        ) {
            Text(
                text = recipe.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Row {
                if (recipe.author == "Gemini")
                {
                    Row {
                        Text(
                            text = "By",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = " ${recipe.author} ",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .graphicsLayer(alpha = 0.99f)
                                .drawWithCache {
                                    val brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF4B9BFF), // Light blue
                                            Color(0xFF2D6EDF)  // Darker blue
                                        )
                                    )
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(brush, blendMode = BlendMode.SrcAtop)
                                    }
                                }
                                .padding(bottom = 8.dp)
                        )

                        // Gemini Icon
                        Image(
                            painter = painterResource(id = R.drawable.google_gemini_icon), // Replace with your drawable resource
                            contentDescription = "Gemini Icon",
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .graphicsLayer {
                                    alpha = 0.5f
                                }
                        )
                    }
                } else {
                    Text(
                        text = "By ${recipe.author}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = " | ${recipe.recipeCategory}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        if (recipe.video?.isNotEmpty() == true) {
            IconButton(
                onClick = {
                    //Go to Recipe Video URL
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.video))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Bookmark Icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
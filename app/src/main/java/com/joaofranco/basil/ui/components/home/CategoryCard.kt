package com.joaofranco.basil.ui.components.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.joaofranco.basil.ui.components.all.ShimmerBrush
import com.joaofranco.basil.ui.theme.BasilTheme

@Composable
fun CategoryCard(
    label: String,
    imageUrl: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                onClick = { navController.navigate("categoryDetail/$label") },
                indication = null, // Disable ripple for the entire card
                interactionSource = remember { MutableInteractionSource() }
            ) // Make the whole card clickable but without ripple effect
    ) {
        Box(
            modifier = Modifier
                .size(115.dp) // Set the size of the Box
                .clip(CircleShape) // Clip the Box to be circular
                .background(MaterialTheme.colorScheme.surfaceContainer) // Background color
                .clickable(
                    onClick = {
                        // Navigate to the DetailPage with the label
                        navController.navigate("categoryDetail/$label")
                    },
                    indication = LocalIndication.current, // Default ripple indication
                    interactionSource = remember { MutableInteractionSource() } // Interaction tracking
                )
        ) {
            // Add padding inside the circle
            Box(
                modifier = Modifier
                    .padding(20.dp) // Set your desired padding
                    .fillMaxSize() // Fill the Box to maintain the circular shape
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Fit, // This will crop the image to fill the circle
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun CategoryLoadingSkeleton(
    showShimmer: Boolean,
    modifier: Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(115.dp)
                .clip(CircleShape) // Use CircleShape here
                .background(
                    ShimmerBrush(
                        targetValue = 1300f,
                        showShimmer = showShimmer
                    )
                )
        )
        Text(
            text = " ", // Placeholder text
            modifier = Modifier
                .padding(top = 8.dp)
                .width(35.dp)
                .background(
                    ShimmerBrush(targetValue = 1300f, showShimmer = showShimmer)
                ), // Apply shimmer brush
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryCardPreview() {
    BasilTheme {
        // Using rememberNavController for preview only
        CategoryCard(
            label = "Placeholder",
            imageUrl = "https://www.themealdb.com/ingredient/1-Chicken",
            navController = rememberNavController()
        )
    }
}
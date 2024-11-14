package com.joaofranco.basil.ui.components.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.joaofranco.basil.data.model.Recipe
import com.joaofranco.basil.viewmodel.RecipeViewModel
import com.joaofranco.basil.ui.components.all.ShimmerBrush


@Composable
fun RecipeCard(
    fullWidth: Boolean? = false,
    modifier: Modifier = Modifier,
    recipe: Recipe,
    viewModel: RecipeViewModel,
    navController: NavController,
    isBookmarked: Boolean,
    onBookmarkClick: (String) -> Unit // Callback to handle bookmark clicks
) {
    Card(
        modifier = modifier
            .clickable {
                viewModel.updateSelectedRecipe(recipe)
                navController.navigate("recipeDetail") // Navigate using recipe ID
            }
            .then(
                if (fullWidth == true) Modifier.fillMaxWidth() else Modifier.width(300.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val imageUrl = recipe.image ?: "https://ai.google/build/assets/images/cropped-Screenshot_2023-12-06_at_2.08.24PM.png"
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .build()
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Crop,
                contentDescription = recipe.title // Provide a description if available for accessibility
            )

            IconButton(
                onClick = { onBookmarkClick(recipe.id) }, // Call the bookmark function
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(15))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Grade else Icons.Outlined.Grade,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 16.dp)
        ) {
            Text(
                text = recipe.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "By " + recipe.author,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 2.dp),
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun CardSkeletonItem(
    showShimmer: Boolean,
    fullWidth: Boolean?,
    modifier: Modifier,
    width: Dp = 300.dp,
    height: Dp = 180.dp,
    titleHeight: Dp = 20.dp,
) {
    Card(
        modifier = modifier
            .then(
                if (fullWidth == true) Modifier.fillMaxWidth() else Modifier.width(width)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.1f) // Slightly transparent background
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .background(MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.2f)) // Placeholder for image
                    .background(ShimmerBrush()) // Placeholder for image
            )
            Spacer(modifier = Modifier.height(14.dp))
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp)
            ) {
                Text(
                    text = " ", // Placeholder text
                    modifier = Modifier
                        .height(titleHeight)
                        .width(225.dp)
                        .background(MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.2f)) // Placeholder for image
                        .background(ShimmerBrush()),
                    fontSize = 20.sp
                )

                //Author
                Text(
                    text = " ", // Placeholder text
                    modifier = Modifier
                        .height(titleHeight)
                        .width(100.dp)
                        .padding(top = 4.dp)
                        .background(MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.2f)) // Placeholder for image
                        .background(ShimmerBrush()),
                    fontSize = 16.sp
                )
            }
        }
    }
}
package com.joaofranco.basil.ui.screens

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.joaofranco.basil.R
import com.joaofranco.basil.data.model.Recipe
import com.joaofranco.basil.ui.components.recipeDetail.*
import com.joaofranco.basil.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(navController: NavController, viewModel: RecipeViewModel) {
    val selectedRecipe by viewModel.selectedRecipe.observeAsState()
    val initialBookmarkState = selectedRecipe?.let { viewModel.isBookmarked(it.id) } ?: false
    var isBookmarked by remember { mutableStateOf(initialBookmarkState) }
    val context = LocalContext.current

    //Status Bar Color
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = false
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }


    DisposableEffect(Unit) {
        onDispose {
            // Reset to default system bar color when exiting
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = !isDarkTheme
            )
        }
    }

    // Function to share the recipe
    fun shareRecipe(recipe: Recipe) {
        val shareIntent = Intent().apply {
            //URL
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this " + recipe.title + " recipe, I found on Basil: " + recipe.url
            )
            type = "text/html"
        }
        startActivity(context, Intent.createChooser(shareIntent, "Share Recipe"), null)
    }

    // Render UI only if a recipe is selected
    selectedRecipe?.let { recipe ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Add the header image as the first item in the LazyColumn
            item {
                Box {
                    // Recipe Image
                    RecipeImage(
                        imageUrl = recipe.image
                            ?: "https://ai.google/build/assets/images/cropped-Screenshot_2023-12-06_at_2.08.24PM.png"
                    )

                    // Top Bar over the image
                    RecipeDetailTopAppBar(
                        isBookmarked = isBookmarked,
                        onBackClick = { navController.popBackStack() },
                        onBookmarkClick = {
                            isBookmarked = !isBookmarked
                            viewModel.toggleBookmark(recipe)
                        },
                        viewModel = viewModel,
                        recipe = recipe,
                        onShareClick = {
                            shareRecipe(recipe)
                        }
                    )
                }
            }

            // Remaining content items
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    RecipeHeader(recipe)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                    RecipeTimeInfo(recipe)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                    IngredientsList(recipe.ingredients)
                    InstructionsList(recipe.instructions)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                    if (recipe.url != null && recipe.author != "Gemini") {
                        RecipePublisher(recipe.url, context)
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .padding(bottom = 16.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row {
                                    Text(
                                        text = "Recipe Generated by Gemini ",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
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

                                Text(
                                    text = "Verify ingredients and instructions before cooking",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.secondary,
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
                                        .padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    } ?: run {
        // Display a message or a progress indicator if no recipe is selected
        Text("No recipe selected", style = MaterialTheme.typography.bodyMedium)
    }
}
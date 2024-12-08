package com.joaofranco.basil.ui.components.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.joaofranco.basil.data.model.Recipe
import com.joaofranco.basil.viewmodel.RecipeViewModel

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    viewModel: RecipeViewModel,
    navController: NavController
) {
    val bookmarkedRecipes by viewModel.bookmarkedRecipes.collectAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)
    val context = LocalContext.current

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        // If still loading, show skeleton items
        if (isLoading) {
            // Show skeletons according to the number of items you expect
            items(5) { index ->   // Change 5 to the number of skeletons you want to show
                CardSkeletonItem(
                    showShimmer = true,
                    fullWidth = false,
                    modifier = Modifier.padding(
                        start = if (index == 0) 16.dp else 0.dp,
                        end = if (index == recipes.size - 1) 16.dp else 0.dp
                    )
                )
            }
        } else {
            items(recipes.size) { index ->
                val recipe = recipes[index]
                val isBookmarked = bookmarkedRecipes.any { it.id == recipe.id }

                RecipeCard(
                    recipe = recipe,
                    viewModel = viewModel,
                    navController = navController,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = {
                        viewModel.toggleBookmark(
                            recipe,
                            onSuccess = {
                                // Show a Toast message on success
                                if (bookmarkedRecipes.contains(recipe)) {
                                    Toast.makeText(context, "Recipe Unbookmarked Successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Recipe Bookmarked Successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onFailure = { errorMessage ->
                                // Show a Toast message on failure
                                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier.padding(
                        start = if (index == 0) 16.dp else 0.dp,
                        end = if (index == recipes.size - 1) 16.dp else 0.dp
                    )
                )
            }
        }
    }
}
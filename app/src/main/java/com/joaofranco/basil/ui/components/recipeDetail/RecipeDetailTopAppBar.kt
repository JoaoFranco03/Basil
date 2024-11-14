package com.joaofranco.basil.ui.components.recipeDetail

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.joaofranco.basil.data.model.Recipe
import com.joaofranco.basil.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailTopAppBar(
    isBookmarked: Boolean,
    viewModel: RecipeViewModel,
    recipe: Recipe,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = "", color = MaterialTheme.colorScheme.onPrimary) },
        navigationIcon = {
            IconButton(onClick = {
                onBackClick()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = Color.White
                )
            }
        },
        actions = {
//If recipe is in viewModel.MyRecipes, show remove icon
            if (viewModel.isRecipeInMyRecipes(recipe)) {
                IconButton(
                    onClick = {
                        viewModel.removeLocallyCreatedRecipe(recipe)
                        onBackClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }
            IconButton(onClick = { onShareClick() }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = Color.White
                )
            }
            IconButton(onClick = { onBookmarkClick() }) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Grade else Icons.Outlined.Grade,
                    contentDescription = "Bookmark",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,  // Transparent background
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
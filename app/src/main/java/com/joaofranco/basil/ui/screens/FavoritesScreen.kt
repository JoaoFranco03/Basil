package com.joaofranco.basil.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.joaofranco.basil.ui.components.all.EmptyState
import com.joaofranco.basil.ui.components.home.RecipeCard
import com.joaofranco.basil.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    val bookmarkedRecipes by viewModel.bookmarkedRecipes.collectAsState() // Collect the StateFlow
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current // Get context for showing Toast

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Favorites",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        if (bookmarkedRecipes.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Grade,
                title = "No Favorites Yet!",
                subtitle = "Browse recipes and add your favorites here.",
                buttonText = "Browse Recipes",
                onButtonClick = {
                    navController.navigate("home") // Navigate to the home screen
                },
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Show the bookmarked recipes
                items(bookmarkedRecipes.size) { index ->
                    val recipe = bookmarkedRecipes[index]
                    // Show the recipe card
                    RecipeCard(
                        fullWidth = true,
                        recipe = recipe,
                        viewModel = viewModel,
                        navController = navController,
                        isBookmarked = true,
                        onBookmarkClick = {
                            // Toggle the bookmark state
                            viewModel.toggleBookmark(
                                recipe,
                                onSuccess = {
                                    // Show a Toast message on success
                                    if (bookmarkedRecipes.contains(recipe)) {
                                        Toast.makeText(
                                            context,
                                            "Recipe Unbookmarked Successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Recipe Bookmarked Successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onFailure = { errorMessage ->
                                    // Show a Toast message on failure
                                    Toast.makeText(
                                        context,
                                        "Error: $errorMessage",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        },
                        modifier = Modifier.padding(bottom = if (index == bookmarkedRecipes.size - 1) 94.dp else 0.dp)
                    )
                }
            }
        }
    }
}
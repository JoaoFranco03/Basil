package com.joaofranco.basil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.joaofranco.basil.ui.components.home.RecipeCard
import com.joaofranco.basil.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, viewModel: RecipeViewModel, modifier: Modifier = Modifier) {
    val bookmarkedRecipes by viewModel.bookmarkedRecipes.collectAsState() // Collect the StateFlow
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

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
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection), // Add nestedScroll here
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            //Show the bookmarked recipes
            items(bookmarkedRecipes.size) { index ->
                val recipe = bookmarkedRecipes[index]
                //Show the recipe card
                RecipeCard(
                    fullWidth = true,
                    recipe = recipe,
                    viewModel = viewModel,
                    navController = navController,
                    isBookmarked = true,
                    onBookmarkClick = {
                        //Toggle the bookmark state
                        viewModel.toggleBookmark(recipe)
                    },
                    modifier = Modifier.padding(bottom = if (index == bookmarkedRecipes.size - 1) 94.dp else 0.dp)
                )
            }
        }
    }
}
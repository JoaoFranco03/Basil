package com.joaofranco.basil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.joaofranco.basil.ui.components.all.EmptyState
import com.joaofranco.basil.ui.components.home.RecipeCard
import com.joaofranco.basil.viewmodel.RecipeViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 16.dp)
                    .height(56.dp),
                placeholder = { Text("Search recipes...") },
                trailingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
            )

            if (searchQuery.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Search,
                    title = "Search Recipes",
                    subtitle = "Enter a recipe name, category, or ingredient to search",
                    modifier = Modifier.padding(bottom = 94.dp)
                )
            } else if (searchResults.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Search,
                    title = "No Results Found",
                    subtitle = "Try searching with different keywords",
                    modifier = Modifier.padding(bottom = 94.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults.size) { index ->
                        val recipe = searchResults[index]
                        RecipeCard(
                            fullWidth = true,
                            recipe = recipe,
                            viewModel = viewModel,
                            navController = navController,
                            isBookmarked = viewModel.isBookmarked(recipe.id),
                            onBookmarkClick = {
                                viewModel.toggleBookmark(
                                    recipe,
                                    onSuccess = {},
                                    onFailure = {}
                                )
                            },
                            modifier = Modifier.padding(
                                bottom = if (index == searchResults.size - 1) 94.dp else 0.dp
                            )
                        )
                    }
                }
            }
        }
    }
}
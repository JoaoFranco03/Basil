package com.joaofranco.basil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.joaofranco.basil.data.model.CategoryViewModel
import com.joaofranco.basil.ui.components.home.CategoryList
import com.joaofranco.basil.ui.components.home.RecipeList
import com.joaofranco.basil.ui.components.home.Section
import com.joaofranco.basil.viewmodel.FirebaseAuthViewModel
import com.joaofranco.basil.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, recipeViewModel: RecipeViewModel, categoryViewModel: CategoryViewModel, authViewModel: FirebaseAuthViewModel, modifier: Modifier = Modifier) {
    val recipes by recipeViewModel.recipes.collectAsState() // Collect the StateFlow
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    // Recipes List
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Hello ${authViewModel.user.value?.displayName?.split(" ")?.first() ?: "User"}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Box(modifier = Modifier.padding(bottom = 82.dp)) { // Adjust bottom padding as needed
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandIn { IntSize(width = 1, height = 1) }
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            // Get Random Recipe
                            val recipe = recipeViewModel.getRandomRecipe()
                            recipeViewModel.updateSelectedRecipe(recipe)
                            navController.navigate("recipeDetail")
                        },
                        icon = {
                            Icon(Icons.Filled.Casino, contentDescription = "Get Random Recipe")
                        },
                        text = {
                            Text(text = "I'm Feeling Lucky")
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection), // Add nestedScroll here
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Section("Categories:") {
                    CategoryList(categoryViewModel, navController = navController)
                }
            }
            item {
                var recipesQuick = recipes.filter { it.tags.contains("Quick") }
                Section("Quick Recipes:") {
                    RecipeList(recipesQuick, recipeViewModel, navController)
                }
            }
            item {
                var recipesPortuguese = recipes.filter { it.tags.contains("Portuguese") }
                Section("Portuguese Recipes:") {
                    RecipeList(recipesPortuguese, recipeViewModel, navController)
                }
            }
            item {
                var recipesEasy = recipes.filter { it.tags.contains("Easy") }
                Section("Easy Recipes:") {
                    RecipeList(recipesEasy, recipeViewModel, navController)
                }
            }
            item {
                var recipesVegetarian = recipes.filter { it.tags.contains("Vegetarian") }
                Section("Vegetarian Recipes:", Modifier.padding(bottom = 152.dp)) {
                    RecipeList(recipesVegetarian, recipeViewModel, navController)
                }
            }
        }
    }
}
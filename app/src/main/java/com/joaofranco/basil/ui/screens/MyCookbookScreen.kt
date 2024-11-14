package com.joaofranco.basil.ui.screens

import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.joaofranco.basil.ui.components.home.RecipeCard
import com.joaofranco.basil.viewmodel.FirebaseAuthViewModel
import com.joaofranco.basil.viewmodel.RecipeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCookbookScreen(viewModel: RecipeViewModel, navController: NavController) {
    val myRecipes by viewModel.myRecipes.collectAsState() // Collect the StateFlow
    val bookmarkedRecipes by viewModel.bookmarkedRecipes.collectAsState() // Collect the StateFlow
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val authViewModel = FirebaseAuthViewModel()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "My Recipes",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    // Add a delete all button
                    IconButton(onClick = {
                        // Implement mass delete logic here
                        viewModel.deleteAllRecipes()
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete All Recipes")
                    }

                    //Log out button
                    IconButton(onClick = {
                        // Implement log out logic here
                        authViewModel.signOut()
                        //Navigate to onboarding screen and clear the backstack
                        navController.navigate("onboarding") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Log Out")
                    }
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
                        // Navigate to RecipeCreationScreen
                        navController.navigate("aiRecipes")
                    },
                        icon = {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = "Generate Recipe")
                        },
                        text = {
                            Text(text = "Generate Recipe")
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
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Show the bookmarked recipes
            items(myRecipes.size) { index ->
                val recipe = myRecipes[index]
                // Show the recipe card
                RecipeCard(
                    fullWidth = true,
                    recipe = recipe,
                    viewModel = viewModel,
                    navController = navController,
                    isBookmarked = bookmarkedRecipes.contains(recipe),
                    onBookmarkClick = {
                        // Toggle the bookmark state
                        viewModel.toggleBookmark(recipe)
                    },
                    modifier = Modifier.padding(bottom = if (index == myRecipes.lastIndex) 94.dp else 0.dp)
                )
            }
        }
    }
}
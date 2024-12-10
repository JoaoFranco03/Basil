package com.joaofranco.basil.ui.screens

import android.graphics.drawable.Icon
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.joaofranco.basil.ui.components.all.EmptyState
import com.joaofranco.basil.ui.components.home.RecipeCard
import com.joaofranco.basil.viewmodel.FirebaseAuthViewModel
import com.joaofranco.basil.viewmodel.RecipeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCookbookScreen(viewModel: RecipeViewModel, navController: NavController) {
    val myRecipes by viewModel.myRecipes.collectAsState() // Collect the StateFlow
    val bookmarkedRecipes by viewModel.bookmarkedRecipes.collectAsState() // Collect the StateFlow
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current // Get context for showing Toast
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedRecipes by viewModel.selectedRecipes.collectAsState()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    if (isSelectionMode) {
                        Text(
                            "${selectedRecipes.size} Selected",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.displaySmall.fontSize,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            "My Recipes",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.displaySmall.fontSize,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (isSelectionMode) {
                            viewModel.toggleSelectionMode()
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (isSelectionMode && selectedRecipes.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.deleteSelectedRecipes(
                                onSuccess = {
                                    Toast.makeText(context, "Selected recipes deleted", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Selected")
                        }
                    } else {
                        if (myRecipes.isNotEmpty()) {
                            // Add a delete all button only if there are recipes
                            IconButton(onClick = {
                                // Implement mass delete logic here
                                viewModel.deleteAllUserRecipes(
                                    onSuccess = {
                                        // Show a Toast message on success
                                        Toast.makeText(
                                            context,
                                            "Recipes Deleted Successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack() // Navigate back after successful deletion
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
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete All Recipes")
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Box { // Adjust bottom padding as needed
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
        if (myRecipes.isEmpty()) {
            EmptyState(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                title = "No Recipes Yet!",
                subtitle = "Create your own recipes and they will appear here.",
                buttonText = "Generate Some Recipes",
                onButtonClick = {
                    navController.navigate("aiRecipes") // Navigate to the recipe creation screen
                }
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
                items(myRecipes.size) { index ->
                    val recipe = myRecipes[index]
                    RecipeCard(
                        fullWidth = true,
                        recipe = recipe,
                        viewModel = viewModel,
                        navController = navController,
                        isBookmarked = bookmarkedRecipes.contains(recipe),
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
                        onLongClick = {
                            if (!isSelectionMode) {
                                viewModel.toggleSelectionMode()
                                viewModel.toggleRecipeSelection(recipe.id)
                            }
                        },
                        isSelectionMode = isSelectionMode,
                        isSelected = selectedRecipes.contains(recipe.id),
                        modifier = Modifier.padding(bottom = if (index == myRecipes.lastIndex) 94.dp else 0.dp)
                    )
                }
            }
        }
    }
}
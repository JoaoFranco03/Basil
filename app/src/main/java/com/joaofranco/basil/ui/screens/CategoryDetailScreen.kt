package com.joaofranco.basil.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joaofranco.basil.data.model.CategoryViewModel
import com.joaofranco.basil.data.model.Recipe
import com.joaofranco.basil.ui.components.home.CardSkeletonItem
import com.joaofranco.basil.ui.components.home.RecipeCard
import com.joaofranco.basil.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailPage(label: String, recipesViewModel: RecipeViewModel, navController: NavController) {
    LaunchedEffect(label) {
        recipesViewModel.getRecipesByCategory(label)
    }

    // Observing state for loading and category-specific recipes
    val categoryRecipes by recipesViewModel.categoryRecipes.observeAsState(emptyList())
    val isLoading by recipesViewModel.isLoading.observeAsState(true)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        label,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection), // Add nestedScroll here
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Show the bookmarked recipes
            if (isLoading) {
                items(7) {
                    CardSkeletonItem(isLoading, true, Modifier)
                }
            } else {
                if (categoryRecipes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.EggAlt,
                                    contentDescription = "Error Icon",
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Nothing’s cooking here!",
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    fontSize = 28.sp,
                                    lineHeight = 40.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Looks like we couldn’t find any recipes here. Try a different category, or maybe it’s time to get creative in the kitchen!",
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(categoryRecipes) { index, recipe ->
                        // Show the recipe card
                        RecipeCard(
                            fullWidth = true,
                            recipe = recipe,
                            viewModel = recipesViewModel,
                            navController = navController,
                            isBookmarked = recipesViewModel.isBookmarked(recipe.id),
                            onBookmarkClick = {
                                // Toggle the bookmark state
                                recipesViewModel.toggleBookmark(recipe)
                            }
                        )
                    }
                }
            }
        }
    }
}
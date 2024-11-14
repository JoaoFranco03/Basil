package com.joaofranco.basil.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.joaofranco.basil.data.model.Category
import com.joaofranco.basil.data.model.CategoryViewModel
import com.joaofranco.basil.viewmodel.RecipeViewModel

@Composable
fun CategoryList(
    viewModel: CategoryViewModel,
    navController: NavController
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)

    LazyRow(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(), // Ensuring width constraint
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            items(5) { index ->
                CategoryLoadingSkeleton(
                    showShimmer = isLoading,
                    modifier = Modifier.padding(
                        start = if (index == 0) 16.dp else 0.dp,
                        end = if (index == 4) 16.dp else 0.dp
                    )
                )
            }
        } else {
            items(categories) { category ->
                CategoryCard(
                    label = category.name,
                    imageUrl = category.imageUrl,
                    navController = navController,
                    modifier = Modifier.padding(
                        start = if (category == categories.first()) 16.dp else 0.dp,
                        end = if (category == categories.last()) 16.dp else 0.dp
                    )
                )
            }
        }
    }
}
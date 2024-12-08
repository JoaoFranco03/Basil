package com.joaofranco.basil.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.joaofranco.basil.data.model.Ingredient
import com.joaofranco.basil.data.model.Recipe
import com.joaofranco.basil.ui.theme.BasilTheme
import com.joaofranco.basil.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeForm(
    recipe: Recipe = Recipe(),
    viewModel: RecipeViewModel,
    navController: NavController,
    onSave: (Recipe) -> Unit
) {
    var title by remember { mutableStateOf(recipe.title) }
    var recipeCategory by remember { mutableStateOf(recipe.recipeCategory) }
    var description by remember { mutableStateOf(recipe.description) }
    var prepTime by remember { mutableStateOf(recipe.prepTime.toString()) }
    var cookTime by remember { mutableStateOf(recipe.cookTime.toString()) }
    var servings by remember { mutableStateOf(recipe.servings.toString()) }
    var imageUrl by remember { mutableStateOf(recipe.image ?: "") }
    var ingredients by remember { mutableStateOf(recipe.ingredients.toMutableList()) }

    var newIngredientName by remember { mutableStateOf("") }
    var newIngredientQuantity by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Add Recipe") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        // Wrap the LazyColumn with proper height constraints
        LazyColumn(
            modifier = Modifier
                .fillMaxSize() // Ensure that LazyColumn gets a bounded size
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                // Recipe Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Recipe Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                // State to manage dropdown expanded state
                var expanded by remember { mutableStateOf(false) }

                // State for selected category
                var recipeCategory by remember { mutableStateOf<String?>(null) }

                // Predefined categories
                val categories = listOf("Main Course", "Cocktails", "Appetizers", "Desserts", "Side Dishes", "Breakfast")

                // OutlinedTextField to toggle the dropdown menu
                OutlinedTextField(
                    value = recipeCategory ?: "Select Category",  // Display the selected category or a placeholder
                    onValueChange = { /* No-op, handled by dropdown */ },
                    label = { Text("Category") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },  // Toggle dropdown when clicked
                    readOnly = true,  // Make it non-editable
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    }
                )

                // DropdownMenu for selecting category
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                recipeCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            item {
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    maxLines = 5
                )
            }

            item {
                // Prep time
                OutlinedTextField(
                    value = prepTime,
                    onValueChange = { prepTime = it },
                    label = { Text("Prep Time (min)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
            }

            item {
                // Cook time
                OutlinedTextField(
                    value = cookTime,
                    onValueChange = { cookTime = it },
                    label = { Text("Cook Time (min)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
            }

            item {
                // Servings
                OutlinedTextField(
                    value = servings,
                    onValueChange = { servings = it },
                    label = { Text("Servings") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
            }

            item {
                // Image URL
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
            }

            // Ingredients Section
            item {
                Text("Ingredients", style = MaterialTheme.typography.headlineSmall)
            }

            item {
                // Ingredient Input Section
                IngredientInput(
                    newIngredientName = newIngredientName,
                    newIngredientQuantity = newIngredientQuantity,
                    onIngredientNameChange = { newIngredientName = it },
                    onIngredientQuantityChange = { newIngredientQuantity = it },
                    onAddIngredient = {
                        if (newIngredientName.isNotEmpty() && newIngredientQuantity.isNotEmpty()) {
                            val newIngredient = Ingredient(newIngredientName, newIngredientQuantity)
                            // Update the ingredients list with a new list to trigger recomposition
                            ingredients = ingredients.toMutableList().apply {
                                add(newIngredient)
                            }
                            newIngredientName = ""
                            newIngredientQuantity = ""
                        }
                    }
                )
            }

            items(ingredients) { ingredient ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${ingredient.ingredient} - ${ingredient.quantity}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        // Remove ingredient by creating a new list
                        ingredients = ingredients.toMutableList().apply {
                            remove(ingredient)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove Ingredient",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Button(
                    onClick = {
                        val authorName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown Author"

                        val newRecipe = recipe.copy(
                            title = title,
                            //Author -> Get the current user's name using the code:
                            author =  authorName,
                            description = description,
                            prepTime = prepTime.toInt(),
                            cookTime = cookTime.toInt(),
                            servings = servings.toInt(),
                            image = imageUrl,
                            ingredients = ingredients // Pass the updated list of ingredients
                        )
                        viewModel.addRecipeToUserRecipes(
                            newRecipe,
                            onSuccess = {
                                navController.popBackStack()
                            },
                            onFailure = {
                                // Handle error
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Recipe")
                }
            }
        }
    }
}

@Composable
fun IngredientInput(
    newIngredientName: String,
    newIngredientQuantity: String,
    onIngredientNameChange: (String) -> Unit,
    onIngredientQuantityChange: (String) -> Unit,
    onAddIngredient: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Ingredient Name Input
        OutlinedTextField(
            value = newIngredientName,
            onValueChange = onIngredientNameChange,
            label = { Text("Ingredient Name") },
            modifier = Modifier.weight(1f),
            isError = newIngredientName.isEmpty(),
            singleLine = true,
            supportingText = {
                if (newIngredientName.isEmpty()) {
                    Text("Please enter an ingredient name", color = MaterialTheme.colorScheme.error)
                }
            }
        )

        // Ingredient Quantity Input
        OutlinedTextField(
            value = newIngredientQuantity,
            onValueChange = onIngredientQuantityChange,
            label = { Text("Quantity") },
            modifier = Modifier.weight(1f),
            isError = newIngredientQuantity.isEmpty(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            supportingText = {
                if (newIngredientQuantity.isEmpty()) {
                    Text("Please enter a quantity", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // Add Ingredient Button
    Button(
        onClick = onAddIngredient,
        modifier = Modifier.fillMaxWidth(),
        enabled = newIngredientName.isNotEmpty() && newIngredientQuantity.isNotEmpty()
    ) {
        Text("Add Ingredient")
    }
}
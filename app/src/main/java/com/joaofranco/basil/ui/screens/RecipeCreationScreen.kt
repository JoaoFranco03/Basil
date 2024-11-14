package com.joaofranco.basil.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.joaofranco.basil.data.model.Recipe
import com.joaofranco.basil.data.model.Ingredient

@Composable
fun RecipeCreationScreen(onSubmit: (Recipe) -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var recipeCategory by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var ingredientsList by remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    var ingredientName by remember { mutableStateOf("") }
    var ingredientQuantity by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("") }
    var cookTime by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("") }
    var bookmarked by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Photo picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri // Store selected image URI
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Create New Recipe", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = recipeCategory,
                onValueChange = { recipeCategory = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            // Image Picker Button and Display
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image")
            }

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(uri)
                            .build()
                    ),
                    contentDescription = "Selected Recipe Image",
                    modifier = Modifier
                        .size(200.dp)
                )
            }

            // Ingredients Section
            Text("Ingredients", fontWeight = FontWeight.Bold)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = ingredientName,
                    onValueChange = { ingredientName = it },
                    label = { Text("Ingredient") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = ingredientQuantity,
                    onValueChange = { ingredientQuantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (ingredientName.isNotBlank() && ingredientQuantity.isNotBlank()) {
                            ingredientsList.add(Pair(ingredientName.trim(), ingredientQuantity.trim()))
                            ingredientName = ""
                            ingredientQuantity = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            }


            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Recipe URL") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = prepTime,
                onValueChange = { prepTime = it },
                label = { Text("Prep Time (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cookTime,
                onValueChange = { cookTime = it },
                label = { Text("Cook Time (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = servings,
                onValueChange = { servings = it },
                label = { Text("Servings") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = bookmarked,
                    onCheckedChange = { bookmarked = it }
                )
                Text("Bookmark this recipe")
            }

            Button(
                onClick = {
                    // Create Recipe object from inputs
                    val newRecipe = Recipe(
                        title = title,
                        author = author,
                        description = description,
                        recipeCategory = recipeCategory,
                        image = imageUri.toString(),
                        url = url,
                        ingredients = ingredientsList.map { Ingredient(it.first, it.second) },
                        instructions = instructions.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        prepTime = prepTime.toIntOrNull() ?: 0,
                        cookTime = cookTime.toIntOrNull() ?: 0,
                        totalTime = (prepTime.toIntOrNull() ?: 0) + (cookTime.toIntOrNull() ?: 0),
                        servings = servings.toIntOrNull() ?: 0,
                        bookmarked = bookmarked
                    )
                    onSubmit(newRecipe) // Submit the new Recipe object
                },
            ) {
                Text("Create Recipe")
            }
        }
    }
}
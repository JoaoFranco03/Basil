package com.joaofranco.basil.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ChildFriendly
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.gson.GsonBuilder
import com.joaofranco.basil.data.model.Recipe
import com.joaofranco.basil.ui.theme.BasilTheme
import com.joaofranco.basil.viewmodel.PromptHistoryManager
import com.joaofranco.basil.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIRecipes(viewModel: RecipeViewModel, navController: NavController) {
    val context = LocalContext.current
    val promptHistoryManager = remember { PromptHistoryManager(context) }
    var promptHistory by remember { mutableStateOf(promptHistoryManager.loadPromptHistory()) }

    val coroutineScope = rememberCoroutineScope()
    var responseString by remember { mutableStateOf("") } // Use state variable for response
    val userRequest = remember { mutableStateOf("") }
    val gsonBuilder = GsonBuilder()
    gsonBuilder.setLenient()
    val gson = gsonBuilder.create()
    // Gemini Model
    val generativeModel = GenerativeModel(
        "gemini-1.5-flash",
        "AIzaSyCHXS1BPLw0psrY_1N8vHxeSg41xn8XHp4",
        generationConfig = generationConfig {
            temperature = 2f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "application/json"
        },
    )

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF6482DF),
            Color(0xFFB36E8B)
        )
    )

    var isGeneratingResponse by remember { mutableStateOf(false) }

    var toastMessage by remember { mutableStateOf<String?>(null) } // State for Toast message

    fun fetchImageUrl(recipeTitle: String): String {
        val formattedTitle = recipeTitle.replace(" ", "-")
        return "https://image.pollinations.ai/prompt/$formattedTitle.png"
    }

    fun sendRequest() {
        if (userRequest.value.isBlank()) {
            Log.w("sendRequest", "User request is blank.")
            Toast.makeText(context, "Please enter ingredients.", Toast.LENGTH_SHORT).show()
            return
        }

        coroutineScope.launch {
            Log.d("sendRequest", "Sending request with prompt: ${userRequest.value}")
            val updatedHistory = promptHistory + userRequest.value
            promptHistory = updatedHistory
            promptHistoryManager.savePromptHistory(updatedHistory)

            isGeneratingResponse = true
            try {
                val prompt =
                    """Create a recipe based on the following request: ${userRequest.value}.
                    Instructions:
                    - **Specific Ingredients**: If the request specifies particular ingredients with the word "only," use **only** those ingredients and avoid adding any others.
                    - **General Requests**: For general requests (e.g., "I need a recipe for dinner" or "Something impressive for guests"), design a recipe that best matches the request, taking into account common meal contexts.
                    - **Dietary Restrictions**: If any dietary restrictions are included (e.g., "vegetarian," "gluten-free"), make sure the recipe follows those guidelines.
                    - **Occasions**: If the request mentions a specific occasion (e.g., "birthday," "holiday"), create a recipe suitable for that event.
                    - **Random Requests**: For random requests (e.g., "something unique," "a surprise"), create a recipe that is creative and unexpected.
                    - **Cuisine Type**: If a specific cuisine is mentioned (e.g., "Italian," "Mexican"), create a recipe inspired by that cuisine style.
                    - **Meal Type**: If the request specifies a meal type (e.g., "breakfast," "dessert"), ensure the recipe is suitable for that meal.
                    
                    The recipe should follow this exact structure:
                    
                    {
                        "author": "Gemini",
                        "title": "",
                        "description": "",
                        "servings": 1,
                        "totalTime": 0,
                        "prepTime": 0,
                        "cookTime": 0,
                        "recipeCategory": "",
                        "ingredients": [
                            {
                                "ingredient": "",
                                "quantity": ""
                            }
                        ],
                        "instructions": [""],
                        "tags": [""]
                    }
                    
                    Make sure the recipe is realistic, feasible with the ingredients, and aligns strictly with the given format.
                """
                Log.d("sendRequest", "Generated prompt: $prompt")

                val response = generativeModel?.generateContent(prompt)

                if (response == null) {
                    Log.e("sendRequest", "No response from generative model.")
                    Toast.makeText(context, "Failed to get response. Check model configuration.", Toast.LENGTH_SHORT).show()
                }

                if (response != null) {
                    responseString = response.text.toString()
                }
                Log.d("sendRequest", "Received response: $responseString")

                if (responseString.isNotBlank()) {
                    responseString = responseString.replace("```json", "").trim()
                    val recipe = gson.fromJson(responseString, Recipe::class.java)

                    if (recipe.title.isNotBlank()) {
                        val imageUrl = fetchImageUrl(recipe.title)
                        recipe.image = imageUrl
                        viewModel.addLocallyCreatedRecipe(recipe)
                        viewModel.updateSelectedRecipe(recipe)
                        navController.navigate("recipeDetail")
                    } else {
                        Log.w("sendRequest", "Generated recipe has no title or is incomplete.")
                        Toast.makeText(context, "The generated recipe is incomplete. Try again!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("sendRequest", "Response string is blank.")
                    Toast.makeText(context, "No response received. Please try again!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("sendRequest", "Exception occurred during recipe generation", e)
                Toast.makeText(context, "Error generating recipe. Please try again!", Toast.LENGTH_SHORT).show()
            } finally {
                isGeneratingResponse = false
                Log.d("sendRequest", "Request processing completed")
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Gemini Chef",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        // Use gradient for title text
                        style = TextStyle(
                            brush = gradient,
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            // Replace your existing Row with LazyRow
            val recipes = listOf(
                "Give me a Recipe to Impress My Friends" to (Icons.Filled.People to "Give me a recipe to impress my friends"),
                "Give me a Recipe for a Romantic Dinner" to (Icons.Filled.Favorite to "Give me a recipe for a romantic dinner"),
                "Give me a Vegan Recipe Idea" to (Icons.Filled.EnergySavingsLeaf to "Give me a vegan recipe"),
                "Give me a Recipe for a Quick Weeknight Dinner" to (Icons.Filled.Fastfood to "Give me a quick weeknight dinner recipe"),
                "Give me a Recipe for a Gluten-Free Dessert" to (Icons.Filled.Cake to "Give me a gluten-free dessert recipe"),
                "Give me a Recipe for a Kids' Snack" to (Icons.Filled.ChildCare to "Give me some kids' snack ideas"),
                "Give me a Recipe for a Classic Italian Dish" to (Icons.Filled.LocalPizza to "Give me a recipe for a classic Italian dish"),
                "Give me a Recipe for a Healthy Smoothie" to (Icons.Filled.LocalDrink to "Give me a healthy smoothie recipe"),
                "Give me a Recipe for a Comfort Food" to (Icons.Filled.Restaurant to "Give me a recipe for comfort food")
            )
            if (isGeneratingResponse) {
                val infiniteTransition = rememberInfiniteTransition()
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center // Center the content in both x and y directions
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(48.dp)
                            .rotate(rotation)
                    ) {
                        drawArc(
                            brush = gradient,
                            startAngle = 0f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 104.dp), // Add bottom padding to avoid overlap with input area
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Prompt History
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Recents:",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .weight(1f) // Take available space
                        ) {
                            items(promptHistory.reversed().take(3)) { prompt ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Icon
                                    Icon(
                                        imageVector = Icons.Filled.History,
                                        contentDescription = "Prompt",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(5.dp)
                                    )

                                    Text(
                                        text = prompt,
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 14.sp
                                        ),
                                        modifier = Modifier.padding(5.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(recipes) { index, (title, iconWithPrompt) ->
                                val (icon, prompt) = iconWithPrompt // Deconstruct the Pair
                                GeminiCard(
                                    title = title,
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = title,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .background(
                                                    MaterialTheme.colorScheme.surfaceBright,
                                                    shape = CircleShape
                                                )
                                                .padding(5.dp)
                                        )
                                    },
                                    onClick = {
                                        userRequest.value = prompt // Use the prompt directly for the request
                                        sendRequest()
                                    },
                                    modifier = Modifier.padding(
                                        start = if (index == 0) 16.dp else 0.dp, // Add start padding only for the first item
                                        end = if (index == recipes.lastIndex) 16.dp else 0.dp // Add end padding only for the last item
                                    )
                                )
                            }
                        }
                    }
                }
            }
            // Input area at bottom
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Text field
                    TextField(
                        value = userRequest.value,
                        onValueChange = { userRequest.value = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        placeholder = {
                            Text(
                                text = "I want a recipe for...",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                    )

                    // Send button
                    IconButton(onClick = {
                        sendRequest()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Microphone",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    toastMessage?.let { message ->
        Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
        toastMessage = null // Clear the message after showing
    }
}

@Composable
fun GeminiCard(
    title: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .height(120.dp)
            .width(180.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                lineHeight = 20.sp
            )

            Box(
                modifier = Modifier.align(Alignment.End)
            ) {
                icon()
            }
        }
    }
}
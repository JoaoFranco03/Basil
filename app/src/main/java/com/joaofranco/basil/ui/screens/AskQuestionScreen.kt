package com.joaofranco.basil.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Fireplace
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.GsonBuilder
import com.joaofranco.basil.data.Message
import com.joaofranco.basil.ui.components.ChatBubble
import com.joaofranco.basil.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskQuestionScreen(navController: NavController, viewModel: RecipeViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var question by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    val gsonBuilder = GsonBuilder()
    gsonBuilder.setLenient()
    val gson = gsonBuilder.create()

    val generativeModel = GenerativeModel(
        "gemini-1.5-flash-8b",
        //Add your Gemini API key
        "",
        generationConfig = generationConfig {
            temperature = 0.95f // Slightly increased for more creative responses
            topP = 0.95f
            topK = 40
        },
    )

    var messages by remember { mutableStateOf(listOf<Message>()) }
    var isLoading by remember { mutableStateOf(false) }
    var isLjubomirStanisticMode by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    
    // Effect to scroll to bottom when new messages are added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .imePadding(),  // Removed navigationBarsPadding() from here
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (isLjubomirStanisticMode) {
                        Text(
                            "Sous Chef Ljubomir \uD83D\uDE08",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    } else {
                        Text(
                            "Sous Chef",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        isLjubomirStanisticMode = !isLjubomirStanisticMode 
                        if (isLjubomirStanisticMode) {
                            Toast.makeText(context, "Get ready to be flambéed!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Ljubomir Stanisic Mode Disabled", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = if (isLjubomirStanisticMode) Icons.Filled.LocalFireDepartment else Icons.Outlined.LocalFireDepartment,
                            contentDescription = if (isLjubomirStanisticMode) "Disable Ljubo Mode" else "Enable Ljubo Mode"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
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
                        value = question,
                        onValueChange = { question = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        placeholder = {
                            Text(
                                text = "Ask a question about the recipe...",
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
                        if (question.isNotBlank()) {
                            coroutineScope.launch {
                                val currentQuestion = question // Store the current question
                                val userMessage = Message(question, isUser = true)
                                messages = messages + userMessage
                                val loadingMessage = Message("", isUser = false, isLoading = true)
                                messages = messages + loadingMessage
                                isLoading = true
                                question = ""

                                try {
                                    // Create chat history string
                                    val chatHistory = messages.dropLast(1).joinToString("\n") { msg ->
                                        if (msg.isUser) "User: ${msg.content}"
                                        else "Assistant: ${msg.content}"
                                    }

                                    val prompt = buildString {
                                        append("Previous conversation:\n")
                                        if (chatHistory.isNotBlank()) {
                                            append("$chatHistory\n")
                                        }
                                        append("Current Request:\n")
                                        append("User: $currentQuestion\n")
                                        append("Recipe Information: ${gson.toJson(viewModel.selectedRecipe.value)}\n")
                                        if (isLjubomirStanisticMode) {
                                            append("Answer the user's question as if you were Ljubomir Stanisic, the charismatic and blunt chef from Hell's Kitchen. \n")
                                            append("Be direct, knowledgeable, and use a mix of humor and strong opinions, and a little bit of ROAST\n")
                                        } else {
                                            append("Answer the User Question\n")
                                        }
                                        append("Answer only questions related to cooking.\n")
                                    }

                                    Log.d("AskQuestionScreen", "Prompt: $prompt")
                                    
                                    val generatedResponse = generativeModel.generateContent(prompt)
                                    val responseText = generatedResponse.text?.replace("  ", " ") ?: "Oops! I seemed to have dropped my cooking spoon! \uD83E\uDD44 Could you try asking again?"
                                    Log.d("AskQuestionScreen", "Response: $responseText")
                                    
                                    messages = messages.dropLast(1) + Message(
                                        responseText,
                                        isUser = false
                                    )
                                } catch (e: Exception) {
                                    Log.e("AskQuestionScreen", "Error generating response", e)
                                    messages = messages.dropLast(1) + Message(
                                        "Sorry, there was an error generating the response.",
                                        isUser = false
                                    )
                                    Toast.makeText(context, "Error generating response.", Toast.LENGTH_SHORT).show()
                                }
                                isLoading = false
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
            }
            
            if (messages.isEmpty()) {
                Text(
                    "Ask any question about the recipe!",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
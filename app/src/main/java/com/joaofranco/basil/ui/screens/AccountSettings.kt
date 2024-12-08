package com.joaofranco.basil.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pentagon
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.joaofranco.basil.ui.components.account.StatItem
import com.joaofranco.basil.ui.components.account.StatsCard
import com.joaofranco.basil.ui.components.home.RecipeCard
import com.joaofranco.basil.ui.components.home.RecipeList
import com.joaofranco.basil.ui.components.home.Section
import com.joaofranco.basil.ui.components.home.SectionWithAction
import com.joaofranco.basil.viewmodel.FirebaseAuthViewModel
import com.joaofranco.basil.viewmodel.RecipeViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettings(
    navController: NavController,
    authViewModel: FirebaseAuthViewModel,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    val bookmarkedRecipes by viewModel.bookmarkedRecipes.collectAsState() // Collect the StateFlow
    val myRecipes by viewModel.myRecipes.collectAsState() // Collect the StateFlow
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val creationTimestamp = authViewModel.user.value?.metadata?.creationTimestamp
    val year = creationTimestamp?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy"))
    } ?: "N/A"

    // State to handle dialog visibility
    var showDialog by remember { mutableStateOf(false) }
    // State to manage password input
    var password by remember { mutableStateOf("") }
    // State to show error message
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Scaffold layout with LazyColumn
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            // User's display name
                            authViewModel.user.value?.displayName ?: "N/A",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.displaySmall.fontSize,
                            color = MaterialTheme.colorScheme.primary
                        )
                        // User's email
                        Text(
                            authViewModel.user.value?.email ?: "N/A",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    // Log out button
                    IconButton(onClick = {
                        authViewModel.signOut()
                        navController.navigate("onboarding") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out")
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
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection), // Add nestedScroll here
        ) {
            item {
                // Stats card item with dynamically provided data
                StatsCard(
                    stats = listOf(
                        StatItem(
                            icon = Icons.Filled.AccountCircle,
                            label = "User Since:",
                            value = year
                        ),
                        StatItem(
                            icon = Icons.Filled.Grade,
                            label = "Favorites:",
                            value = bookmarkedRecipes.size.toString()
                        ),
                        StatItem(
                            icon = Icons.Filled.Edit,
                            label = "Creations:",
                            value = myRecipes.size.toString()
                        )
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // Favourites (Icon + Text)
            item {
                SectionWithAction(
                    navController,
                    "favorites",
                    "Favorites"
                ) {
                    if (bookmarkedRecipes.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No Favorites Yet!", color = MaterialTheme.colorScheme.secondary)
                        }
                    } else {
                        RecipeList(
                            bookmarkedRecipes,
                            viewModel,
                            navController
                        )
                    }
                }
            }

            // My Cookbook (Icon + Text)
            item {
                SectionWithAction(
                    navController,
                    "cookbook",
                    "My Cookbook"
                ) {
                    if (myRecipes.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No Recipes Yet!", color = MaterialTheme.colorScheme.secondary)
                        }
                    } else {
                        RecipeList(
                            myRecipes,
                            viewModel,
                            navController
                        )
                    }
                }
            }

            // Delete Account (Icon + Text)
            item {
                Button(
                    onClick = {
                        // Show the dialog to confirm deletion
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .padding(bottom = 84.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    // Icon
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete Account",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Delete Account")
                }
            }
        }
    }

    // Confirmation Dialog for Deleting Account
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // Close the dialog if dismissed
                showDialog = false
            },
            title = {
                Text("Confirm Deletion", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Are you sure you want to permanently delete your account? This action cannot be undone.")
                    Spacer(modifier = Modifier.height(16.dp))
                    // Password input field for reauthentication
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Enter Password to Confirm") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (password.isBlank()) {
                            // Don't proceed with deletion if the password is empty
                            errorMessage = "Password cannot be empty."
                        } else {
                            // Trigger reauthentication
                            val userEmail = authViewModel.user.value?.email ?: ""
                            if (userEmail.isNotEmpty()) {
                                authViewModel.reauthenticateUser(
                                    email = userEmail,
                                    password = password,
                                    onSuccess = {
                                        // After reauthentication, proceed with account deletion
                                        authViewModel.deleteUser(
                                            onSuccess = {
                                                showDialog = false // Close the dialog
                                                authViewModel.signOut() // Log out the user
                                                navController.navigate("onboarding") {
                                                    popUpTo("onboarding") { inclusive = true }
                                                }
                                            },
                                            onFailure = { error ->
                                                // Show the error message
                                                errorMessage = error
                                                showDialog = false // Close the dialog
                                            }
                                        )
                                    },
                                    onFailure = { error ->
                                        // Handle reauthentication failure
                                        errorMessage = error
                                    }
                                )
                            }
                        }
                    },
                    enabled = password.isNotBlank() // Disable button if password is blank
                ) {
                    Text("Yes, Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // Close the dialog without doing anything
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Display an error message if there's an error
    errorMessage?.let {
        Toast.makeText(LocalContext.current, it, Toast.LENGTH_LONG).show()
        errorMessage = null // Reset the error message after showing the toast
    }
}
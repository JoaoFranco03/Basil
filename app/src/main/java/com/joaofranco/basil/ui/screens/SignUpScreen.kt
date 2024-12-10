package com.joaofranco.basil.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.joaofranco.basil.viewmodel.FirebaseAuthViewModel
import com.joaofranco.basil.viewmodel.RecipeViewModel
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, authViewModel: FirebaseAuthViewModel) {
    // Form state
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Error state
    var displayNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var generalError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Sign Up",
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Subheader
            Text(
                text = "Create an account to save your favorite recipes and access them from anywhere",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display Name Field
            OutlinedTextField(
                value = displayName,
                onValueChange = {
                    displayName = it
                    displayNameError = ""  // Clear error on input change
                },
                label = { Text("Display Name") },
                supportingText = {
                    if (displayNameError.isNotEmpty()) Text(displayNameError)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = displayNameError.isNotEmpty()
            )

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""  // Clear error on input change
                },
                label = { Text("Email") },
                supportingText = {
                    if (emailError.isNotEmpty()) Text(emailError)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = emailError.isNotEmpty()
            )

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""  // Clear error on input change
                },
                label = { Text("Password") },
                supportingText = {
                    if (passwordError.isNotEmpty()) Text(passwordError)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError.isNotEmpty()
            )

            // Confirm Password Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = ""  // Clear error on input change
                },
                label = { Text("Confirm Password") },
                supportingText = {
                    if (confirmPasswordError.isNotEmpty()) Text(confirmPasswordError)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = confirmPasswordError.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Button
            Button(
                onClick = {
                    // Reset all errors
                    displayNameError = ""
                    emailError = ""
                    passwordError = ""
                    confirmPasswordError = ""
                    generalError = ""

                    // Validation logic
                    if (displayName.isBlank()) displayNameError = "Name cannot be empty."
                    if (email.isBlank()) emailError = "Email cannot be empty."
                    else if (!authViewModel.isValidEmail(email)) emailError = "Please enter a valid email."
                    if (password.isBlank()) passwordError = "Password cannot be empty."
                    else if (!authViewModel.isValidPassword(password)) passwordError = "Password must be at least 6 characters."
                    else {
                        // Additional password complexity check
                        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$"
                        if (!Pattern.matches(passwordRegex, password)) {
                            passwordError = "Password must contain uppercase, lowercase, and a number."
                        }
                    }

                    if (confirmPassword.isBlank()) confirmPasswordError = "Please confirm your password."
                    else
                    if (confirmPassword != password) confirmPasswordError = "Passwords do not match."

                    // If no field errors, attempt signup
                    if (displayNameError.isEmpty() && emailError.isEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                        authViewModel.signUp(
                            name = displayName,
                            email = email,
                            password = password,
                            onSuccess = { navController.navigate("home") },
                            onFailure = { generalError = it }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            // General Error Message
            if (generalError.isNotEmpty()) {
                Text(generalError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
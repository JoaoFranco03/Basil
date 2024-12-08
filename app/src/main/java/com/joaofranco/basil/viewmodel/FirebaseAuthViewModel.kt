package com.joaofranco.basil.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import java.util.regex.Pattern
import com.joaofranco.basil.viewmodel.RecipeViewModel

class FirebaseAuthViewModel(private val recipesViewModel: RecipeViewModel) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = mutableStateOf<FirebaseUser?>(null)
    val user: State<FirebaseUser?> = _user

    init {
        _user.value = auth.currentUser  // Get the current user on initialization
        recipesViewModel.loadBookmarkedRecipes()
        recipesViewModel.loadMyRecipes()
        recipesViewModel.getAllRecipes()
    }

    // Helper function to validate email format
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        return Pattern.matches(emailRegex, email)
    }

    // Helper function to validate password strength
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6 // Firebase requires a minimum of 6 characters
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (name.isBlank()) {
            onFailure("Name cannot be empty.")
            return
        }

        if (email.isBlank()) {
            onFailure("Email cannot be empty.")
            return
        }

        if (password.isBlank()) {
            onFailure("Password cannot be empty.")
            return
        }

        if (!isValidEmail(email)) {
            onFailure("Please enter a valid email.")
            return
        }

        if (!isValidPassword(password)) {
            onFailure("Password must be at least 6 characters long.")
            return
        }

        //Password Security
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$"
        if (!Pattern.matches(passwordRegex, password)) {
            onFailure("Password must contain at least one uppercase letter, one lowercase letter, and one number.")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            _user.value = user
                            onSuccess()
                        } else {
                            val errorMessage = profileTask.exception?.message
                                ?: "Sign up failed. Please try again."
                            onFailure(errorMessage)
                        }
                    }
                } else {
                    val errorMessage =
                        task.exception?.message ?: "Sign up failed. Please try again."
                    onFailure(errorMessage)
                }
            }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onFailure("Email and password cannot be empty.")
            return
        }

        if (!isValidEmail(email)) {
            onFailure("Please enter a valid email.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    recipesViewModel.loadBookmarkedRecipes()
                    recipesViewModel.loadMyRecipes()
                    recipesViewModel.getAllRecipes()
                    onSuccess()
                } else {
                    val errorMessage =
                        task.exception?.message ?: "Sign in failed. Please try again."
                    onFailure(errorMessage)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
    }

    fun reauthenticateUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(email, password)

        // Reauthenticate with the user's email and password
        user?.reauthenticate(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    val errorMessage = task.exception?.message
                        ?: "Reauthentication failed. Please try again."
                    onFailure(errorMessage)
                }
            }
    }

    fun deleteUser(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = null
                    onSuccess()
                } else {
                    val errorMessage = task.exception?.message
                        ?: "Failed to delete user. Please try again."
                    onFailure(errorMessage)
                }
            }
    }
}
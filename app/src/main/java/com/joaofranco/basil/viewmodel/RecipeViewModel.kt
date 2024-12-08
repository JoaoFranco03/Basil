package com.joaofranco.basil.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.joaofranco.basil.data.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance() // Initialize Firestore
    private val gson = Gson() // Initialize Gson for serialization

    // All Recipes
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> get() = _recipes

    // Selected Recipe
    private val _selectedRecipe = MutableLiveData<Recipe?>(null)
    val selectedRecipe: LiveData<Recipe?> = _selectedRecipe.asFlow().asLiveData()

    // Bookmarked Recipes
    private val _bookmarkedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val bookmarkedRecipes: StateFlow<List<Recipe>> get() = _bookmarkedRecipes

    // Category-specific Recipes
    private val _categoryRecipes = MutableLiveData<List<Recipe>>()
    val categoryRecipes: LiveData<List<Recipe>> get() = _categoryRecipes

    // Loading State
    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    //Locally Created Recipes (Must be saved in a local database)
    private val _myRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val myRecipes: StateFlow<List<Recipe>> get() = _myRecipes

    // Selection Mode
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode

    private val _selectedRecipes = MutableStateFlow<Set<String>>(emptySet())
    val selectedRecipes: StateFlow<Set<String>> = _selectedRecipes

    // Search-related state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<Recipe>>(emptyList())
    val searchResults: StateFlow<List<Recipe>> = _searchResults

    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
        if (!_isSelectionMode.value) {
            clearSelection()
        }
    }

    fun toggleRecipeSelection(recipeId: String) {
        val currentSelection = _selectedRecipes.value.toMutableSet()
        if (currentSelection.contains(recipeId)) {
            currentSelection.remove(recipeId)
        } else {
            currentSelection.add(recipeId)
        }
        _selectedRecipes.value = currentSelection
    }

    fun clearSelection() {
        _selectedRecipes.value = emptySet()
    }

    fun deleteSelectedRecipes(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            viewModelScope.launch {
                val userRecipesRef = db.collection("users").document(user.uid).collection("recipes")
                try {
                    _selectedRecipes.value.forEach { recipeId ->
                        userRecipesRef.document(recipeId).delete().await()
                    }
                    // Clear selection and exit selection mode before reloading
                    _selectedRecipes.value = emptySet()
                    _isSelectionMode.value = false
                    loadMyRecipes() // Refresh the list
                    loadBookmarkedRecipes() // Also refresh bookmarks in case any were deleted
                    onSuccess()
                } catch (e: Exception) {
                    onFailure(e.message ?: "Failed to delete selected recipes.")
                }
            }
        } else {
            onFailure("User is not authenticated.")
        }
    }

    // Get all recipes from Firestore
    fun getAllRecipes() {
        viewModelScope.launch {
            val docRef = db.collection("recipes")

            docRef.get()
                .addOnSuccessListener { documents ->
                    val recipes = mutableListOf<Recipe>()
                    for (document in documents) {
                        val recipe = document.toObject(Recipe::class.java)
                        recipe.id = document.id // Set the id manually
                        recipes.add(recipe)
                        Log.d("Recipe ID", "Recipe ID: ${recipe.id}, Title: ${recipe.title}")
                    }
                    _recipes.value = recipes
                    _isLoading.value = false
                    loadBookmarkedRecipes() // Refresh bookmarks based on loaded recipes
                }
                .addOnFailureListener { exception ->
                    Log.e("RecipeViewModel", "Error getting documents.", exception)
                    _isLoading.value = false
                }
        }
    }

    // Load User Recipes from Firestore
    public fun loadMyRecipes() {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userRecipesRef = db.collection("users").document(user.uid).collection("recipes")
                userRecipesRef.get()
                    .addOnSuccessListener { documents ->
                        val myRecipes = documents.map { document ->
                            document.toObject(Recipe::class.java).apply { id = document.id }
                        }
                        _myRecipes.value = myRecipes
                    }
                    .addOnFailureListener { exception ->
                        Log.e("RecipeViewModel", "Error getting user recipes for user ${user.uid}", exception)
                    }
            }
        }
    }

    // Load Bookmarked Recipes from Firestore, update variable bookmarkedRecipes, and take in consideration user may not have any bookmarked recipes
    fun loadBookmarkedRecipes() {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userBookmarksRef = db.collection("users").document(user.uid).collection("bookmarks")
                userBookmarksRef.get()
                    .addOnSuccessListener { documents ->
                        val bookmarkedRecipeIds = documents.map { it.id }
                        _bookmarkedRecipes.value = _recipes.value.filter { it.id in bookmarkedRecipeIds }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("RecipeViewModel", "Error getting bookmarked recipes for user ${user.uid}", exception)
                    }
            }
        }
    }

    // Toggle the bookmark state of a recipe, taking in consideration the user may not have any bookmarked recipes
    fun toggleBookmark(recipe: Recipe, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userBookmarksRef = db.collection("users").document(user.uid).collection("bookmarks")
            if (_bookmarkedRecipes.value.any { it.id == recipe.id }) {
                userBookmarksRef.document(recipe.id).delete()
                    .addOnSuccessListener {
                        _bookmarkedRecipes.value = _bookmarkedRecipes.value.filter { it.id != recipe.id }
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception.message ?: "Failed to remove recipe from bookmarks.")
                    }
            } else {
                userBookmarksRef.document(recipe.id).set(recipe)
                    .addOnSuccessListener {
                        _bookmarkedRecipes.value = _bookmarkedRecipes.value + recipe
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception.message ?: "Failed to add recipe to bookmarks.")
                    }
            }
        } else {
            onFailure("User is not authenticated.")
        }
    }

    // Update selected recipe
    fun updateSelectedRecipe(recipe: Recipe) {
        _selectedRecipe.value = recipe
        loadMyRecipes()
    }

    // Check if a recipe is bookmarked
    fun isBookmarked(recipeId: String): Boolean {
        // Check if the recipe is in the list of bookmarked recipes
        return _bookmarkedRecipes.value.any { it.id == recipeId }
    }

    // Filter recipes by category
    fun getRecipesByCategory(category: String) {
        // Filter recipes by category
        viewModelScope.launch {
            _isLoading.value = true
            db.collection("recipes").whereEqualTo("recipeCategory", category).get()
                .addOnSuccessListener { documents ->
                    _categoryRecipes.value = documents.map { document ->
                        document.toObject(Recipe::class.java).apply { id = document.id }
                    }
                    Log.d("RecipeViewModel", "Found ${_categoryRecipes.value!!.size} recipes with category $category")
                    _isLoading.value = false
                }
                .addOnFailureListener {
                    Log.e("RecipeViewModel", "Error getting category recipes.", it)
                    _isLoading.value = false
                }
        }
    }

    //Choose a random recipe
    fun getRandomRecipe(): Recipe {
        // Return a random recipe from the list
        return _recipes.value.random()
    }

    fun addRecipeToUserRecipes(recipe: Recipe, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        // Add the recipe to the user's collection in Firestore
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userRecipesRef = db.collection("users").document(user.uid).collection("recipes")
            userRecipesRef.add(recipe)
                .addOnSuccessListener {
                    loadMyRecipes() // Refresh the list of user recipes
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Failed to add recipe to user recipes.")
                }
        } else {
            onFailure("User is not authenticated.")
        }
    }

    // Remove User Recipe from Firestore
    fun deleteUserRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userRecipesRef = db.collection("users").document(user.uid).collection("recipes")
            userRecipesRef.document(recipe.id).delete()
                .addOnSuccessListener {
                    loadMyRecipes() // Refresh the list of user recipes
                    loadBookmarkedRecipes()
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Failed to delete recipe from user recipes.")
                }
        } else {
            onFailure("User is not authenticated.")
        }
    }

    //Check if a recipe is in the user's recipes
    fun isUserRecipe(recipeId: String): Boolean {
        return _myRecipes.value.any { it.id == recipeId }
    }

    // Delete all User Recipes
    fun deleteAllUserRecipes(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userRecipesRef = db.collection("users").document(user.uid).collection("recipes")
            userRecipesRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        userRecipesRef.document(document.id).delete()
                    }
                    loadMyRecipes() // Refresh the list of user recipes
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Failed to delete user recipes.")
                }
        } else {
            onFailure("User is not authenticated.")
        }
    }

    // Update search query and trigger search
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        performSearch(query)
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            val normalizedQuery = query.toString().lowercase(Locale.getDefault()).trim()
            _searchResults.value = _recipes.value.filter { recipe ->
                recipe.title.toString().lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                recipe.recipeCategory.toString().lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                recipe.ingredients.any { 
                    it.toString().lowercase(Locale.getDefault()).contains(normalizedQuery) 
                }
            }
        }
    }
}
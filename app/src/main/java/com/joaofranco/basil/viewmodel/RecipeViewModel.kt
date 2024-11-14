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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.joaofranco.basil.data.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val sharedPrefs = application.getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
    private val bookmarkedKey = "bookmarked_recipes"
    private val myRecipesKey = "my_recipes"
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

    init {
        loadBookmarkedRecipes()
        loadMyRecipes()
        getAllRecipes()
    }

    // Load bookmarks from SharedPreferences
    private fun loadBookmarkedRecipes() {
        val bookmarkedIds = sharedPrefs.getStringSet(bookmarkedKey, emptySet()) ?: emptySet()
        viewModelScope.launch {
            val allRecipes = _recipes.value
            _bookmarkedRecipes.value = allRecipes.filter { it.id in bookmarkedIds }
        }
    }

    // Load locally created recipes from SharedPreferences
    private fun loadMyRecipes() {
        val myRecipe = sharedPrefs.getString(myRecipesKey, null)
        if (myRecipe != null) {
            val type = object : TypeToken<List<Recipe>>() {}.type
            _myRecipes.value = gson.fromJson(myRecipe, type) ?: emptyList()
        }
    }

    // Get all recipes from Firestore
    private fun getAllRecipes() {
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

    // Update selected recipe
    fun updateSelectedRecipe(recipe: Recipe) {
        _selectedRecipe.value = recipe
        Log.d("RecipeViewModel", "Selected Recipe: ${recipe.title}")
    }

    // Add or remove bookmarked recipe
    fun toggleBookmark(recipe: Recipe) {
        val isBookmarked = _bookmarkedRecipes.value.contains(recipe)
        val updatedBookmarks = if (isBookmarked) {
            _bookmarkedRecipes.value - recipe
        } else {
            _bookmarkedRecipes.value + recipe
        }

        _bookmarkedRecipes.value = updatedBookmarks
        saveBookmarksToSharedPrefs(updatedBookmarks)
    }

    // Save bookmarks to SharedPreferences
    private fun saveBookmarksToSharedPrefs(bookmarkedRecipes: List<Recipe>) {
        val bookmarkedIds = bookmarkedRecipes.map { it.id }.toSet()
        sharedPrefs.edit().putStringSet(bookmarkedKey, bookmarkedIds).apply()
    }

    // Check if a recipe is bookmarked
    fun isBookmarked(recipeId: String): Boolean {
        return _bookmarkedRecipes.value.any { it.id == recipeId }
    }

    // Filter recipes by category
    fun getRecipesByCategory(category: String) {
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
        return _recipes.value.random()
    }

    // Save locally created recipes to SharedPreferences
    private fun saveLocallyCreatedRecipesToSharedPrefs() {
        val json = gson.toJson(_myRecipes.value)
        sharedPrefs.edit().putString(myRecipesKey, json).apply()
    }

    // Add Locally Created Recipe to the list
    fun addLocallyCreatedRecipe(recipe: Recipe, isAIGenerated: Boolean = false) {
        val updatedList = _myRecipes.value.toMutableList()
        recipe.isAIGenerated = isAIGenerated
        updatedList.add(recipe)
        _myRecipes.value = updatedList
        saveLocallyCreatedRecipesToSharedPrefs() // Call to save after updating
    }

    // Remove Locally Created Recipe from the list
    fun removeLocallyCreatedRecipe(recipe: Recipe) {
        val updatedList = _myRecipes.value.toMutableList()
        updatedList.remove(recipe)
        _myRecipes.value = updatedList
        saveLocallyCreatedRecipesToSharedPrefs() // Call to save after updating
    }

    //CHeck if a recipe is in MyRecipes
    fun isRecipeInMyRecipes(recipe: Recipe): Boolean {
        return _myRecipes.value.contains(recipe)
    }

    // Delete all MY Recipes
    fun deleteAllRecipes() {
        _myRecipes.value = emptyList()
        saveLocallyCreatedRecipesToSharedPrefs()
    }
}
package com.joaofranco.basil.data.model

data class Recipe(
    var id: String = "",
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val recipeCategory: String = "",
    var image: String? = null,
    val url: String? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val tags : List<String> = emptyList(),
    val prepTime: Int = 0,
    val cookTime: Int = 0,
    val totalTime: Int = 0,
    val servings: Int = 0,
    var bookmarked: Boolean = false,
    var video: String? = null,
    var isAIGenerated: Boolean = false
)

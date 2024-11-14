package com.joaofranco.basil.data.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Category(
    val name: String = "",
    val imageUrl: String = ""
)

class CategoryViewModel : ViewModel() {
    private val firebaseFirestore = Firebase.firestore

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories // Expose as StateFlow

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        getAllCategories()
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            val docRef = firebaseFirestore.collection("categories")

            docRef.get()
                .addOnSuccessListener { result ->
                    _categories.value = result.toObjects(Category::class.java) // Load all categories into state
                    _isLoading.value = false
                }
                .addOnFailureListener { exception ->
                    Log.e("CategoryViewModel", "Error getting documents.", exception)
                    _isLoading.value = false
                }
        }
    }
}



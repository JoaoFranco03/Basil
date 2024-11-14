package com.joaofranco.basil.viewmodel

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PromptHistoryManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("prompt_history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Key for SharedPreferences entry
    private val PROMPT_HISTORY_KEY = "prompt_history"

    // Load prompt history from SharedPreferences
    fun loadPromptHistory(): List<String> {
        val json = sharedPreferences.getString(PROMPT_HISTORY_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Save prompt history to SharedPreferences
    fun savePromptHistory(promptHistory: List<String>) {
        val json = gson.toJson(promptHistory)
        sharedPreferences.edit().putString(PROMPT_HISTORY_KEY, json).apply()
    }
}
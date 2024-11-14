package com.joaofranco.basil.utils

import java.net.URI
import java.net.URISyntaxException

class LinkToPublisherName(val url: String) {
    val publisherName: String
        get() {
            return try {
                val host = URI(url).host ?: return "Unknown"
                when {
                    host.contains("allrecipes") -> "All Recipes"
                    host.contains("bbcgoodfood") -> "BBC Good Food"
                    host.contains("bbc.co.uk") -> "BBC Food"
                    host.contains("bonappetit") -> "Bon Appétit"
                    host.contains("delish") -> "Delish"
                    host.contains("epicurious") -> "Epicurious"
                    host.contains("foodnetwork") -> "Food Network"
                    host.contains("seriouseats") -> "Serious Eats"
                    host.contains("simplyrecipes") -> "Simply Recipes"
                    host.contains("tasty") -> "Tasty"
                    host.contains("cooking.nytimes") -> "NYT Cooking"
                    host.contains("justcookwithmichael") -> "Just Cook with Michael"
                    else -> "View Recipe in Browser"
                }
            } catch (e: URISyntaxException) {
                "Unknown"
            }
        }
}
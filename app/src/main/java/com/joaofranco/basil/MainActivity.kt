package com.joaofranco.basil

import BottomNavigationBar
import NavigationComponent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.joaofranco.basil.ui.theme.BasilTheme


class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            BasilTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController)
                    }
                ) {
                    NavigationComponent(navController, Modifier)
                }
            }
        }
    }
}
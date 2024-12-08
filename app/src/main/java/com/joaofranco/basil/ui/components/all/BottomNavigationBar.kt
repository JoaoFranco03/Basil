import android.graphics.ColorFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.joaofranco.basil.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Define the routes where the BottomNavigationBar should be visible
    val navigationBarItemPages = listOf("home", "search", "accountSettings")

    // Get the current backstack entry and observe the destination
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    // Check if the current route is in the list of routes where the BottomNavigationBar should be visible
    val isVisible = currentDestination?.route in navigationBarItemPages

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 150)),
        exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 150)),
    ) {
        NavigationBar {
            NavigationBarItem(
                selected = currentDestination?.route == "home",
                onClick = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { saveState = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text("Home") },
                icon = {
                    if (currentDestination?.route == "home") {
                        androidx.compose.material3.Icon(Icons.Filled.Home, contentDescription = "Home")
                    } else {
                        androidx.compose.material3.Icon(Icons.Outlined.Home, contentDescription = "Home")
                    }
                }
            )
            NavigationBarItem(
                selected = currentDestination?.route == "search",
                onClick = {
                    navController.navigate("search") {
                        popUpTo(navController.graph.startDestinationId) { saveState = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text("Search") },
                icon = {
                    if (currentDestination?.route == "search") {
                        androidx.compose.material3.Icon(Icons.Filled.Search, contentDescription = "Favorites")
                    } else {
                        androidx.compose.material3.Icon(Icons.Outlined.Search, contentDescription = "Favorites")
                    }
                }
            )
            NavigationBarItem(
                selected = currentDestination?.route == "accountSettings",
                onClick = {
                    navController.navigate("accountSettings") {
                        popUpTo(navController.graph.startDestinationId) { saveState = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text("Account") },
                icon = {
                    if (currentDestination?.route == "accountSettings") {
                        androidx.compose.material3.Icon(Icons.Filled.Person, contentDescription = "Account")
                    } else {
                        androidx.compose.material3.Icon(Icons.Outlined.Person, contentDescription = "Account")
                    }
                }
            )
        }
    }
}
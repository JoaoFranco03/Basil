import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.joaofranco.basil.data.model.CategoryViewModel
import com.joaofranco.basil.data.model.Recipe
import com.joaofranco.basil.ui.screens.AIRecipes
import com.joaofranco.basil.ui.screens.AccountSettings
import com.joaofranco.basil.ui.screens.OnboardingScreen
import com.joaofranco.basil.ui.screens.CategoryDetailPage
import com.joaofranco.basil.ui.screens.FavoritesScreen
import com.joaofranco.basil.ui.screens.HomeScreen
import com.joaofranco.basil.ui.screens.MyCookbookScreen
import com.joaofranco.basil.ui.screens.RecipeCreationScreen
import com.joaofranco.basil.ui.screens.RecipeDetailScreen
import com.joaofranco.basil.ui.screens.RecipeForm
import com.joaofranco.basil.ui.screens.SearchScreen
import com.joaofranco.basil.ui.screens.SignInScreen
import com.joaofranco.basil.ui.screens.SignUpScreen
import com.joaofranco.basil.ui.screens.AskQuestionScreen
import com.joaofranco.basil.viewmodel.FirebaseAuthViewModel
import com.joaofranco.basil.viewmodel.RecipeViewModel

private fun AnimatedContentTransitionScope<*>.slideInLeft() =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))

private fun AnimatedContentTransitionScope<*>.slideOutRight() =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))

private fun AnimatedContentTransitionScope<*>.defaultEnterTransition() =
    fadeIn(animationSpec = tween(300))

private fun AnimatedContentTransitionScope<*>.defaultExitTransition() =
    fadeOut(animationSpec = tween(300))

@Composable
fun NavigationComponent(navController: NavHostController, modifier: Modifier) {
    val recipeViewModel: RecipeViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val authViewModel = FirebaseAuthViewModel(recipeViewModel)

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.user.value != null) "home" else "onboarding"
    ) {
        // Authentication screen route
        composable("onboarding") {
            OnboardingScreen(
                navController = navController
            )
        }

        //Sign up screen
        composable(
            "signUp",
            enterTransition = { slideInLeft() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultEnterTransition() },
            popExitTransition = { slideOutRight() }
        ) {
            SignUpScreen(navController,authViewModel)
        }

        //Sign up screen
        composable(
            "signIn",
            enterTransition = { slideInLeft() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultEnterTransition() },
            popExitTransition = { slideOutRight() }
        ) {
            SignInScreen(navController,authViewModel)
        }

        // Home screen
        composable(
            "home",
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() }
        ) {
            HomeScreen(navController, recipeViewModel, categoryViewModel, authViewModel, modifier = modifier)
        }

        //Search screen
        composable(
            "search",
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() }
        ) {
            SearchScreen(navController, recipeViewModel)
        }

        // Other screens (e.g., favorites, cookbook, etc.) remain unchanged
        composable(
            "favorites",
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() }
        ) {
            FavoritesScreen(navController, recipeViewModel)
        }

        //recipeCreation
        composable(
            "recipeCreation",
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() }
        ) {
            RecipeForm(Recipe(), recipeViewModel, navController)
        }

        composable(
            "cookbook",
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() }
        ) {
            MyCookbookScreen(recipeViewModel, navController)
        }

        composable(
            "recipeDetail",
            enterTransition = { slideInLeft() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultEnterTransition() },
            popExitTransition = { slideOutRight() }
        ) {
            RecipeDetailScreen(navController, recipeViewModel)
        }

        composable(
            "categoryDetail/{categoryName}",
            enterTransition = { slideInLeft() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultEnterTransition() },
            popExitTransition = { slideOutRight() }) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            if (categoryName != null) {
                CategoryDetailPage(categoryName, recipeViewModel, navController)
            }
        }

        // AI Recipes screen
        composable(
            "aiRecipes",
            enterTransition = { slideInLeft() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultEnterTransition() },
            popExitTransition = { slideOutRight() }
        ) {
            AIRecipes(recipeViewModel, navController)  // Display the AI Recipes screen
        }

        // Account screen
        composable(
            "accountSettings",
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() }
        ) {
            AccountSettings(navController, authViewModel, recipeViewModel)
        }

        // Ask Question screen
        composable(
            "askQuestion",
            enterTransition = { slideInLeft() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultEnterTransition() },
            popExitTransition = { slideOutRight() }
        ) {
            AskQuestionScreen(navController, recipeViewModel)
        }
    }
}
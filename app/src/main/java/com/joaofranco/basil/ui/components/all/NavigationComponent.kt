import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.joaofranco.basil.data.model.CategoryViewModel
import com.joaofranco.basil.ui.screens.AIRecipes
import com.joaofranco.basil.ui.screens.AccountSettings
import com.joaofranco.basil.ui.screens.OnboardingScreen
import com.joaofranco.basil.ui.screens.CategoryDetailPage
import com.joaofranco.basil.ui.screens.FavoritesScreen
import com.joaofranco.basil.ui.screens.HomeScreen
import com.joaofranco.basil.ui.screens.MyCookbookScreen
import com.joaofranco.basil.ui.screens.RecipeCreationScreen
import com.joaofranco.basil.ui.screens.RecipeDetailScreen
import com.joaofranco.basil.ui.screens.SignInScreen
import com.joaofranco.basil.ui.screens.SignUpScreen
import com.joaofranco.basil.viewmodel.FirebaseAuthViewModel
import com.joaofranco.basil.viewmodel.RecipeViewModel

@Composable
fun NavigationComponent(navController: NavHostController, modifier: Modifier) {
    val recipeViewModel: RecipeViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val authViewModel = viewModel<FirebaseAuthViewModel>()

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
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) {
            SignUpScreen(navController,authViewModel)
        }

        //Sign up screen
        composable(
            "signIn",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) {
            SignInScreen(navController,authViewModel)
        }

        // Home screen
        composable(
            "home",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            HomeScreen(navController, recipeViewModel, categoryViewModel, authViewModel, modifier = modifier)
        }

        // Other screens (e.g., favorites, cookbook, etc.) remain unchanged
        composable(
            "favorites",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            FavoritesScreen(navController, recipeViewModel)
        }

        composable(
            "cookbook",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            MyCookbookScreen(recipeViewModel, navController)
        }

        composable(
            "recipeDetail",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) { backStackEntry ->
            RecipeDetailScreen(navController, recipeViewModel)
        }

        composable(
            "categoryDetail/{categoryName}",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            if (categoryName != null) {
                CategoryDetailPage(categoryName, recipeViewModel, navController)
            }
        }

        // Recipe creation screen
        composable(
            "recipeCreation",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }) { backStackEntry ->
            RecipeCreationScreen(
                onSubmit = { recipe ->
                    recipeViewModel.addLocallyCreatedRecipe(recipe)
                    navController.popBackStack()
                }
            )
        }

        // AI Recipes screen
        composable(
            "aiRecipes",
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) {
            AIRecipes(recipeViewModel, navController)  // Display the AI Recipes screen
        }

        // Account screen
        composable(
            "accountSettings",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            AccountSettings(navController, authViewModel, recipeViewModel)
        }
    }
}
package com.joaofranco.basil.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.joaofranco.basil.R
import com.joaofranco.basil.viewmodel.FirebaseAuthViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(UnstableApi::class)
@Composable
fun VideoBackgroundPlayer(context: Context, videoUri: Uri) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false // Hide media controls
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL // Stretch video to fill screen
            }
        }
    )
}

@Composable
fun OnboardingScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.onboarding_video}")

    //Status Bar Color
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = false
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }


    DisposableEffect(Unit) {
        onDispose {
            // Reset to default system bar color when exiting
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = !isDarkTheme
            )
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // Video Background
        VideoBackgroundPlayer(context, videoUri)

        // Semi-transparent overlay for better readability of UI elements
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x33000000))  // Translucent overlay
                .padding(16.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Logo, Text Fields, and Buttons go here
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
            Text(
                text = "Basil",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 16.dp)
            )

            //Subheader
            Text(
                text = "Your personal recipe assistant \n with a touch of magic",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(320.dp))

            Button(
                onClick = {
                    navController.navigate("signup")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),  // Set height for larger button
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    Icons.Filled.Mail,
                    contentDescription = "Mail Icon",
                    modifier = Modifier.size(24.dp)  // Increase icon size
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Sign Up with Email",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Already have an account? Sign in
            Row(
                // Align text and button in the center
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                TextButton(
                    onClick = {
                        navController.navigate("signin")
                    },
                    modifier = Modifier
                        .height(24.dp),  // Adjust height if necessary
                    contentPadding = PaddingValues(0.dp)  // Remove extra padding for a compact look
                ) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "By using Basil, you agree to our ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 1,  // Optional, limit to one line if desired
                    overflow = TextOverflow.Ellipsis  // Display ellipsis if overflow occurs
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lipsum.com/privacy")).also {
                                context.startActivity(it)
                            }
                        },
                        modifier = Modifier
                            .height(24.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Terms of Service",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            textDecoration = TextDecoration.Underline,
                        )
                    }

                    Text(
                        text = " and ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    TextButton(
                        onClick = {
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lipsum.com/privacy")).also {
                                context.startActivity(it)
                            }
                        },
                        modifier = Modifier
                            .height(24.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            textDecoration = TextDecoration.Underline,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(navController = NavController(LocalContext.current))
}
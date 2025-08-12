package com.example.hamrothrift.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R


@Composable
fun WelcomeAndOnboardingScreen(onNavigateToLogin: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentPage) {
                0 -> {
                    // WELCOME PAGE
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Welcome to", fontSize = 28.sp, color = Color.White)

                        Spacer(modifier = Modifier.height(24.dp))

                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(onClick = { currentPage++ }) {
                            Text("Next")
                        }
                    }
                }

                in 1..3 -> {
                    // ONBOARDING PAGES
                    val imageRes = when (currentPage) {
                        1 -> R.drawable.onboardingone
                        2 -> R.drawable.onboardingtwo
                        3 -> R.drawable.onboardingthree
                        else -> R.drawable.onboardingone
                    }

                    val text = when (currentPage) {
                        1 -> "Discover Unique Finds"
                        2 -> "Explore Categories"
                        3 -> "Get Started Today"
                        else -> ""
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Onboarding $currentPage",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(text, fontSize = 22.sp, color = Color.White)

                        Spacer(modifier = Modifier.height(48.dp))

                        if (currentPage < 3) {
                            IconButton(onClick = { currentPage++ }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        } else {
                            Button(onClick = onNavigateToLogin) {
                                Text("Next")
                            }
                        }
                    }
                }
            }
        }
    }
}
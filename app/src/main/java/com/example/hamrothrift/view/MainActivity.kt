package com.example.mythriftstore

import WelcomeAndOnboardingScreen
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mythriftstore.ui.theme.MyThriftStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyThriftStoreTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WelcomeAndOnboardingScreen(
                        onNavigateToLogin = {
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            finish() // Optional: disable back press to welcome
                        }
                    )

                }
            }
        }
    }
}
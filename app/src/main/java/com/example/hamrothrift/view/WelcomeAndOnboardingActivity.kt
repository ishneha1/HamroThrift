package com.example.hamrothrift.view


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R
import com.example.hamrothrift.view.theme.ui.theme.HamroThriftTheme
import com.example.hamrothrift.view.theme.ui.theme.bg
import com.example.hamrothrift.view.theme.ui.theme.buttton
import com.example.hamrothrift.view.theme.ui.theme.text

class WelcomeAndOnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HamroThriftTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeAndOnboardingBody(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeAndOnboardingBody(modifier: Modifier = Modifier) {
    var currentPage by remember { mutableIntStateOf(0) }
    val font = FontFamily(
        Font(R.font.handmade)
    )
    val context = LocalContext.current
    val activity = context as? Activity

    var value by remember { mutableIntStateOf(0) }

    LaunchedEffect(value) {
        value==1
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = bg
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentPage) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Welcome to", fontSize = 35.sp, color = text,
                            fontFamily = font)
                        Spacer(modifier = Modifier.height(25.dp))
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(45.dp))
                        Row(modifier= Modifier
                            .fillMaxWidth()
                            , horizontalArrangement = Arrangement.End
                        , verticalAlignment = Alignment.CenterVertically) {
                            Button(onClick = { currentPage++ },
                                    colors = ButtonDefaults.buttonColors(
                                    containerColor = buttton,
                                contentColor = Color.Black),
                                modifier = Modifier
                                    .width(100.dp)) {
                                Text("Next",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                    )
                            }
                        }

                    }

                }
                in 1..3 -> {
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
                        Text(text, style = TextStyle(
                            color = Color.Black,
                            fontSize = 25.sp,
                            fontFamily = font,
                            fontWeight = FontWeight.SemiBold
                        ))
                        Spacer(modifier = Modifier.height(48.dp))
                        if (currentPage < 3) {
                            Row(modifier= Modifier
                                .fillMaxWidth()
                                , horizontalArrangement = Arrangement.End
                                , verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { currentPage++ }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Next",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(30.dp)
                                        //.padding(start = 80.dp)
                                    )
                                }
                            }
                        } else {
                            Row(modifier= Modifier
                                .fillMaxWidth()
                                , horizontalArrangement = Arrangement.End
                                , verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = {
                                        val intent = Intent(context, HomepageActivity::class.java)
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = buttton,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier
                                        .width(100.dp)
                                ) {
                                    Text("Next")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
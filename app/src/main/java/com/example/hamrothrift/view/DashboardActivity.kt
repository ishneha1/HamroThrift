package com.example.hamrothrift.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.theme.ui.theme.*

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val gradientColors = listOf(White, deepBlue, Black)
    val font = FontFamily(Font(R.font.handmade))


    // Mode selector state
    val modes = listOf("Buy", "Sell")
    var selectedMode by remember { mutableStateOf("") }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "HamroThrift",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            ),
                            fontSize = 25.sp,
                            fontFamily = font,
                            fontStyle = FontStyle.Italic
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBar),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(bg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(top = 30.dp, start = 20.dp, end = 20.dp),
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp,end=20.dp, top=20.dp),
                    colors = CardDefaults.cardColors(containerColor = card),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Mode Selection",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = font,
                            color = text,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(modes) { mode ->
                                FilterChip(
                                    onClick = {
                                        selectedMode = mode
                                        when (mode) {
                                            "Buy" -> {
                                                val intent = Intent(
                                                    context,
                                                    DashboardActivityBuy::class.java
                                                )
                                                context.startActivity(intent)
                                                activity?.finish()
                                            }

                                            "Sell" -> {
                                                val intent = Intent(
                                                    context,
                                                    DashboardSellActivity::class.java
                                                )
                                                context.startActivity(intent)
                                                activity?.finish()
                                            }
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = "$mode Mode",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    selected = selectedMode == mode,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = buttton,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

                Spacer(modifier = Modifier.height(20.dp))

                // About Section
                Card(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    colors = CardDefaults.cardColors(containerColor = card),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "About HamroThrift",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = font,
                            color = text,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Canvas(
                            modifier = Modifier
                                .height(2.dp)
                                .width(300.dp)
                        ) {
                            drawLine(
                                color = text.copy(alpha = 0.8f),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = size.height
                            )
                        }
                        Spacer(modifier = Modifier.height(25.dp))

                        val descriptions = listOf(
                            "• Discover unique second-hand treasures at unbeatable prices.",
                            "• List your unused items easily and reach real buyers.",
                            "• Connect with the youth-driven circular fashion movement.",
                            "• Trusted platform for Gen-Z vintage finds and thrift needs."
                        )
                        descriptions.forEach {
                            Text(
                                text = it,
                                fontSize = 20.sp,
                                lineHeight = 25.sp,
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = text,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                    }
                }

            }
        }
    }


@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    DashboardBody()
}
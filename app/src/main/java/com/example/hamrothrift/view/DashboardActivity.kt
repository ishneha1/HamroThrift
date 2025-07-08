package com.example.hamrothrift.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.hamrothrift.R
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.theme.ui.theme.White
import com.example.hamrothrift.view.theme.ui.theme.appBar
import com.example.hamrothrift.view.theme.ui.theme.bg
import com.example.hamrothrift.view.theme.ui.theme.buttton
import com.example.hamrothrift.view.theme.ui.theme.deepBlue
import com.example.hamrothrift.view.theme.ui.theme.text

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
    val gradientColors = listOf(White, deepBlue,Black)
    val font = FontFamily(
        Font(R.font.handmade)
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Select Option") }
    val options = listOf("Buy Mode","Sell Mode")
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("HamroThrift"
                    ,style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        ),
                        fontSize = 25.sp,
                        fontFamily = font,
                        fontStyle = FontStyle.Italic
                    )) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBar),

            )

    }) { innerPadding ->
        Column (modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(bg)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, start = 20.dp, end = 20.dp),
                )
                {
                    OutlinedTextField(
                        value = selectedOptionText,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()

                            }
                            .clickable {
                                expanded = true
                            }
                            .clip(RoundedCornerShape(10.dp)),

                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = buttton,
                            disabledIndicatorColor = Color.Black,
                            disabledTextColor = Color.Black
                        ),
                        placeholder = { Text("Select Mode", fontSize = 20.sp
                        , fontWeight = FontWeight.SemiBold)},
                        enabled = false,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }

                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .width(with(LocalDensity.current)
                            { textFieldSize.width.toDp() })
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOptionText = option
                                    expanded = false
                                    if (option == "Buy Mode") {
                                        val intent = Intent(context, DashboardActivityBuy::class.java)
                                        context.startActivity(intent)
                                        activity?.finish()
                                    } else if (option == "Sell Mode") {
                                        val intent =
                                            Intent(context, DashboardSellActivity::class.java)
                                        context.startActivity(intent)
                                        activity?.finish()
                                    }
                                }
                            )

                        }
                    }
                }


            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = "About HamroThrift",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = text,
                    fontFamily = font,
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
                        color = Color.Black
                        , fontStyle = FontStyle.Italic,
                    )
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


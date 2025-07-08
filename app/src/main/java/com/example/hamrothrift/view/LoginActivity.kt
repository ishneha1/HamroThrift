package com.example.hamrothrift.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R
import com.example.hamrothrift.repository.UserRepoImpl
import com.example.hamrothrift.view.theme.bg
import com.example.hamrothrift.view.theme.buttton
import com.example.hamrothrift.view.theme.card
import com.example.hamrothrift.view.theme.text
import com.example.hamrothrift.viewmodel.UserViewModel


class HomepageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomepageBody()

        }
    }
}
@Composable
fun HomepageBody() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    val localEmail: String = sharedPreferences.getString("email", "").toString()
    val localPassword: String = sharedPreferences.getString("password", "").toString()
    var scrollState = rememberScrollState()


    val repo = remember { UserRepoImpl() }
    val userViewModel = remember { UserViewModel(repo) }


    email = localEmail
    password = localPassword

    val font = FontFamily(
        Font(R.font.handmade)
    )

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(bg)
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                colors = CardDefaults.cardColors(card),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 80.dp, end = 20.dp, start = 20.dp, bottom = 60.dp)
                    .clip(shape = RoundedCornerShape(30.dp))

            ) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(top=20.dp,bottom=50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally


                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(200.dp)
                            .width(180.dp)
                            .padding(top = 20.dp)
                            .clip(CircleShape)
                            .clip(shape = RoundedCornerShape(100.dp))
                    )
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Welcome to,",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 25.sp,
                                fontFamily = font, fontWeight = FontWeight.Bold
                            ),
                        )
                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            text = "HamroThrift! ",
                            style = TextStyle(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = font,
                                fontStyle = FontStyle.Italic,
                                color = Color.White

                            )
                        )
                    }


                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(top = 20.dp)
                            .padding(bottom = 20.dp),
                        shape = RoundedCornerShape(12.dp),
                        prefix = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null

                            )
                        },
                        placeholder = { Text("Email") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Black,
                            focusedIndicatorColor = Color.Blue
                        )

                    )
                    OutlinedTextField(

                        value = password,
                        onValueChange = {
                            password = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(bottom = 20.dp),
                        shape = RoundedCornerShape(12.dp),

                        prefix = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null

                            )
                        },
                        suffix = {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisibility) R.drawable.baseline_visibility_off_24
                                    else R.drawable.baseline_visibility_24
                                ),
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    passwordVisibility = !passwordVisibility
                                }

                            )
                        },
                        placeholder = {
                            Text("Password")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.colors(

                            unfocusedIndicatorColor = Color.Black,
                            focusedIndicatorColor = Color.Blue
                        )

                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 15.dp)

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = {
                                    rememberMe = it
                                    rememberMe = true
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Green,
                                    checkmarkColor = Color.White
                                )
                            )
                            Text(
                                "Remember me",
                                color = text
                            )
                        }
                        Text(
                            "Forgot Password?",
                            style = TextStyle(
                                fontStyle = FontStyle.Italic,
                                textDecoration = TextDecoration.Underline
                            ), color = text,
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(context, ForgotPasswordActivity::class.java)
                                    context.startActivity(intent)
                                }


                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center, modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Button(
                            onClick = {
                                userViewModel.login(email, password) { success, message ->

                                    if (success) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                            .show()
                                        val intent =
                                            Intent(context, DashboardActivity::class.java)
                                        context.startActivity(intent)
                                        intent.putExtra("email", email)
                                        intent.putExtra("password", password)
                                        activity?.finish()
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }

                                if (rememberMe) {
                                    editor.putString("email", email)
                                    editor.putString("password", password)
                                    editor.apply()
                                }


                            },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttton,
                                contentColor = Color.Black
                            ), modifier = Modifier
                                .width(250.dp)
                        ) {
                            Text(
                                "Login",
                                style = TextStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            )

                        }

                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 30.dp)
                    ) {
                        Text(
                            "New User?",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = text
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)

                        )

                        Row{
                            Text(
                                "Register Now",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = TextDecoration.Underline
                                ), color = text,
                                modifier = Modifier
                                    .clickable {
                                        val intent =
                                            Intent(context, RegisterActivity::class.java)
                                        context.startActivity(intent)
                                    })

                        }
                    }


                }
            }

        }

    }
}




@Preview(showBackground = true)
@Composable
fun HomepagePreview(){
    HomepageBody()
}
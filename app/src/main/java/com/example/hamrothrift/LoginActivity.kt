package com.example.hamrothrift

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.hamrothrift.ui.theme.Red
import com.example.hamrothrift.ui.theme.Teal
import com.example.hamrothrift.ui.theme.bg
import com.example.hamrothrift.ui.theme.bg1
import com.example.hamrothrift.ui.theme.bg2
import com.example.hamrothrift.ui.theme.bg3
import com.example.hamrothrift.ui.theme.card1
import com.example.hamrothrift.ui.theme.text
import com.example.hamrothrift.ui.theme.text1
import com.example.hamrothrift.ui.theme.text2


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
fun HomepageBody(){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostScope = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    val localEmail: String = sharedPreferences.getString("email", "").toString()
    val localPassword: String = sharedPreferences.getString("password", "").toString()
    var scrollState= rememberScrollState()


    email = localEmail
    password = localPassword

    val font = FontFamily(
        Font(R.font.font)
    )

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(bg1)
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                colors = CardDefaults.cardColors(bg3),
                modifier = Modifier
                    .height(800.dp)
                    .width(900.dp)
                    .padding(top = 50.dp, start = 20.dp, end = 20.dp)
                    .clip(shape = RoundedCornerShape(20.dp))

            ) {
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                        ,horizontalAlignment = Alignment.CenterHorizontally

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
                            modifier = Modifier.height(10.dp)
                        )
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start=10.dp)
                            , verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Welcome to, ",
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    fontFamily = font,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "HamroThrift! ",
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = font,
                                    fontStyle = FontStyle.Italic
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
                                .padding(top = 10.dp)
                                .padding(bottom = 20.dp),
                            shape = RoundedCornerShape(12.dp),
                            prefix = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null

                                )
                            },
                            placeholder = {},
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
                                Text("")
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
                                Text("Remember me")
                            }
                            Text(
                                "Forgot Password?",
                                style = TextStyle(
                                    fontStyle = FontStyle.Italic,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier
                                    .clickable {

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




                                    if (rememberMe) {
                                        editor.putString("email", email)
                                        editor.putString("password", password)
                                        editor.apply()
                                    }

//                                    val intent = Intent(context, DashboardActivity::class.java)
//                                    intent.putExtra("email", email)
//                                    intent.putExtra("password", password)

//                                    context.startActivity(intent)
//                                    activity?.finish()
                                    Toast.makeText(
                                        context,
                                        "Login Success",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()



                                },
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = card1,
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
                                color = Color.White
                            )

                            Row(
                            ) {
                                Text(
                                    "Register Now",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.SemiBold,
                                        textDecoration = TextDecoration.Underline
                                    ), color = Color.White,
                                    modifier = Modifier
                                        .clickable {
//                                            val intent =
//                                                Intent(context, RegisterActivity::class.java)
//                                            context.startActivity(intent)
                                        })

                            }
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 35.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        )

                        {
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(80.dp)
                                    .background(color = Color.Black)
                            )

                            Text(
                                "Sign In with Other Methods",
                                fontSize = 18.sp
                            )
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(80.dp)
                                    .background(color = Color.Black)
                            )
                        }
                        Row {
                            Image(
                                painter = painterResource(R.drawable.email),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(80.dp)
                                    .width(60.dp)
                                    .padding(top = 20.dp)
                                    .clickable {}
                                    .fillMaxWidth()
                                    .clip(CircleShape)

                            )
                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )
                            Image(
                                painter = painterResource(R.drawable.google),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(80.dp)
                                    .width(60.dp)
                                    .padding(top = 20.dp)
                                    .clickable {}
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(100.dp))

                            )
                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )
                            Image(
                                painter = painterResource(R.drawable.insta),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(80.dp)
                                    .width(60.dp)
                                    .padding(top = 20.dp)
                                    .clickable {}
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(100.dp))

                            )
                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )


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
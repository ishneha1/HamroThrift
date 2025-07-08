package com.example.hamrothrift.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R
import com.example.hamrothrift.repository.UserRepoImpl
import com.example.hamrothrift.view.theme.White
import com.example.hamrothrift.view.theme.appBar
import com.example.hamrothrift.view.theme.bg
import com.example.hamrothrift.view.theme.deepBlue
import com.example.hamrothrift.view.ui.theme.HamroThriftTheme
import com.example.hamrothrift.viewmodel.UserViewModel

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgotPasswordBody()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordBody(){

    val repo = remember { UserRepoImpl() }
    val userViewModel = remember { UserViewModel(repo) }
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity  = context as? Activity
    val font = FontFamily(
        Font(R.font.handmade)
    )
    val gradientColors = listOf(White, deepBlue,Black)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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

        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(bg)
            .padding(top=20.dp,start=15.dp)) {
            Text("Forgot Password",
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 25.sp
                ))
            OutlinedTextField(

                value = email,
                onValueChange = {
                    email =it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=25.dp,bottom = 20.dp,end=15.dp),
                shape = RoundedCornerShape(12.dp),
                prefix = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null

                    )
                },
                placeholder = {
                    Text("abc@gmail.com")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = TextFieldDefaults.colors(
                    //focusedContainerColor =Color.Gray.copy(0.2f),

                    //color ko shade dinu
                    unfocusedIndicatorColor = Color.Blue,
                    focusedIndicatorColor = Color.Green
                )
            )
            Button(onClick = {
                userViewModel.forgetPassword(email) { success, message ->
                    if (success) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        activity?.finish()
                    } else {
                        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
                    }
                }
            },
                modifier= Modifier
                    .width(200.dp)
                    .fillMaxWidth(),
                shape= RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text("Reset Password Link")

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview(){
    ForgotPasswordBody()
}

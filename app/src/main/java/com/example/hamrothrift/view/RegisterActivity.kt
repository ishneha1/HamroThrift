package com.example.hamrothrift.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R
import com.example.hamrothrift.model.UserModel
import com.example.hamrothrift.repository.UserRepoImpl
import com.example.hamrothrift.view.theme.ui.theme.bg
import com.example.hamrothrift.view.theme.ui.theme.buttton
import com.example.hamrothrift.view.theme.ui.theme.card
import com.example.hamrothrift.view.theme.ui.theme.text
import com.example.hamrothrift.viewmodel.UserViewModel


class RegisterActivity : ComponentActivity() {
//    lateinit var imageUtils: ImageUtils
//    var selectedImageUri by mutableStateOf<Uri?>(null)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        imageUtils = ImageUtils(this, this)
//        imageUtils.registerLaunchers { uri ->
//            selectedImageUri = uri}
        setContent {
            RegisterActivityBody(
//                selectedImageUri = selectedImageUri,
//                onPickImage = { imageUtils.launchImagePicker() }
            )
        }
    }
}

@Composable
fun RegisterActivityBody()
//    selectedImageUri: Uri?,
//    onPickImage: () -> Unit)
{
    Scaffold { innerPadding ->
        val context = LocalContext.current
        val activity = context as? Activity

        val font = FontFamily(
            Font(R.font.handmade)
        )

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rePassword by remember { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }

        val genderOption = listOf("Male", "Female", "Others")
        var selectedOption by remember { mutableStateOf("") }

        val repo = remember { UserRepoImpl() }
        val userViewModel = remember { UserViewModel(repo) }


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = bg)
                .fillMaxSize()
        ) {
            Card(
                colors = CardDefaults.cardColors(card),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 40.dp, end = 20.dp, start = 20.dp, bottom = 40.dp)
                    .clip(shape = RoundedCornerShape(30.dp))
            ) {

                Text(
                    text = "Create an Account",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font
                    ),
                    modifier = Modifier
                        .padding(top = 70.dp, start = 70.dp)
                )
                Row {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(top = 20.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = {
                            Text(
                                "First Name",
                                fontSize = 15.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Black,
                            focusedIndicatorColor = Color.Blue
                        )

                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(top = 20.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = {
                            Text(
                                "Last Name",
                                fontSize = 15.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Black,
                            focusedIndicatorColor = Color.Blue
                        )

                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 15.dp).fillMaxWidth(),
                ) {
                    genderOption.forEach { text ->
                        Row(
                            modifier = Modifier,

                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = { selectedOption = text }
                            )
                            Text(
                                text = text,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        }
                    }


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
                    placeholder = { Text("E-mail") },
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
                OutlinedTextField(
                    value = rePassword,
                    onValueChange = {
                        rePassword = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 20.dp),
                    shape = RoundedCornerShape(12.dp),

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
                        Text("Re-Enter Password")
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
//                Row(
//                    //horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.baseline_add_a_photo_24),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .padding(start = 20.dp, top = 20.dp)
//                            .size(40.dp)
//                            .clip(
//                                shape = RoundedCornerShape(10.dp)
//                            )
//                    )
//                    Text(
//                        "Upload Profile Picture",
//                        style = TextStyle(
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.SemiBold
//                        ),
//                        modifier = Modifier
//                            .padding(top = 20.dp, start = 20.dp)
//                            .clickable(
//                                indication = null,
//                                interactionSource = remember { MutableInteractionSource() }
//                            ) {
//                                onPickImage()
//                            },
//                        color = text
//                    )
////                            if (selectedImageUri != null) {
////                                AsyncImage(
////                                    model = selectedImageUri,
//                                    contentDescription = "Selected Image",
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentScale = ContentScale.Crop
//                                )
//                            } else {
//                                Image(
//                                    painterResource(R.drawable.images),
//                                    contentDescription = null,
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentScale = ContentScale.Crop
//                                )
//                            }


                Button(
                    onClick = {
                        userViewModel.register(email, password) { success, message, userId ->
                            if (success) {
                                val model = UserModel(
                                    userId,
                                    firstName,
                                    lastName,
                                    selectedOption,
                                    email,
                                    password,
                                    //selectedImageUri.toString()
                                )
                                userViewModel.addUserToDatabase(
                                    userId,
                                    model
                                ) { success, message ->
                                    if (success) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                            .show()
                                        val intent =
                                            Intent(context, HomepageActivity::class.java)
                                        context.startActivity(intent)
                                        activity?.finish()
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }


                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttton,
                        contentColor = text
                    ), modifier = Modifier
                        .width(250.dp)
                        .padding(top = 30.dp, start = 90.dp)
                ) {
                    Text(
                        "Register",
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        ),
                        color = text
                    )

                }
            }



            }
        }
    }




@Preview(showBackground = true)
@Composable
fun RegisterActivityPreview(){
    RegisterActivityBody()
//        selectedImageUri = null,
//        onPickImage = {})

}
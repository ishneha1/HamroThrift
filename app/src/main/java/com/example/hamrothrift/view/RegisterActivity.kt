package com.example.hamrothrift.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import com.example.hamrothrift.R
import com.example.hamrothrift.model.UserModel
import com.example.hamrothrift.repository.UserRepoImpl
import com.example.hamrothrift.utils.ImageUtils
import com.example.hamrothrift.view.theme.ui.theme.bg
import com.example.hamrothrift.view.theme.ui.theme.buttton
import com.example.hamrothrift.view.theme.ui.theme.card
import com.example.hamrothrift.view.theme.ui.theme.text
import com.example.hamrothrift.viewmodel.UserViewModel

class RegisterActivity : ComponentActivity() {
    lateinit var imageUtils: ImageUtils
    var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
        }
        setContent {
            RegisterActivityBody(
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() }
            )
        }
    }
}

@Composable
fun RegisterActivityBody(
    selectedImageUri: Uri? = null,
    onPickImage: (() -> Unit)? = null
) {
    Scaffold { innerPadding ->
        val context = LocalContext.current
        val activity = context as? Activity

        val font = FontFamily(Font(R.font.handmade))

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rePassword by remember { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var isUploading by remember { mutableStateOf(false) }

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
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 50.dp, start = 70.dp)
                )

                // Profile Picture Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onPickImage?.invoke() }
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.baseline_add_a_photo_24),
                                contentDescription = "Add Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(30.dp)
                            )
                        }
                    }

                    Text(
                        text = "Tap to add profile picture",
                        style = TextStyle(fontSize = 12.sp, color = Color.Black),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Row {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("First Name", fontSize = 15.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Black,
                            focusedIndicatorColor = Color.Blue
                        )
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Last Name", fontSize = 15.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Black,
                            focusedIndicatorColor = Color.Blue
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 15.dp)
                        .fillMaxWidth(),
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
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 20.dp, bottom = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("E-mail") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Black,
                        focusedIndicatorColor = Color.Blue
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
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
                    placeholder = { Text("Password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Black,
                        focusedIndicatorColor = Color.Blue
                    )
                )

                OutlinedTextField(
                    value = rePassword,
                    onValueChange = { rePassword = it },
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
                    placeholder = { Text("Re-Enter Password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Black,
                        focusedIndicatorColor = Color.Blue
                    )
                )

                Button(
                    onClick = {
                        if (password != rePassword) {
                            Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isUploading = true
                        userViewModel.register(email, password) { success, message, userId ->
                            if (success) {
                                // Upload image first if selected
                                if (selectedImageUri != null) {
                                    userViewModel.uploadImage(context, selectedImageUri!!) { imageUrl ->
                                        val model = UserModel(
                                            userId,
                                            firstName,
                                            lastName,
                                            selectedOption,
                                            email,
                                            password,
                                            imageUrl ?: ""
                                        )
                                        userViewModel.addUserToDatabase(userId, model) { dbSuccess, dbMessage ->
                                            isUploading = false
                                            if (dbSuccess) {
                                                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(context, HomepageActivity::class.java)
                                                context.startActivity(intent)
                                                activity?.finish()
                                            } else {
                                                Toast.makeText(context, dbMessage, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                } else {
                                    // No image selected, create user without image
                                    val model = UserModel(
                                        userId,
                                        firstName,
                                        lastName,
                                        selectedOption,
                                        email,
                                        password,
                                        ""
                                    )
                                    userViewModel.addUserToDatabase(userId, model) { dbSuccess, dbMessage ->
                                        isUploading = false
                                        if (dbSuccess) {
                                            Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(context, HomepageActivity::class.java)
                                            context.startActivity(intent)
                                            activity?.finish()
                                        } else {
                                            Toast.makeText(context, dbMessage, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                isUploading = false
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isUploading,
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttton,
                        contentColor = text
                    ),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(top = 30.dp, start = 90.dp)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = text
                        )
                    } else {
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
}

@Preview(showBackground = true)
@Composable
fun RegisterActivityPreview() {
    RegisterActivityBody()
}
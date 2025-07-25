package com.example.hamrothrift.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import kotlinx.coroutines.delay

class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashScreenBody()

        }
    }
}

@Composable
fun SplashScreenBody(){
    val context = LocalContext.current
    val activity = context as? Activity
    val sharedPreferences = context.getSharedPreferences(
        "User",
        Context.MODE_PRIVATE
    )

    var value by remember { mutableStateOf(0) }


    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (android.os.Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()





    LaunchedEffect(Unit)
    {
        delay(1000)
        if (value==0){
            val intent = Intent(context, WelcomeAndOnboardingActivity::class.java)
            context.startActivity(intent)
            activity?.finish()
        }
        else {
            val intent = Intent(context, HomepageActivity::class.java)
            context.startActivity(intent)
            activity?.finish()
        }


   }
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding).background(color = Color.White)
                .fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            AsyncImage(
                model = "file:///android_asset/clip.gif",
                contentDescription = null,
                imageLoader = imageLoader,
                modifier = Modifier.size(500.dp)
            )


        }
    }


}
@Preview(showBackground = true)
@Composable
fun PreviewSplashScreen(){
    SplashScreenBody()
}
package com.example.hamrothrift.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.hamrothrift.R
import com.example.hamrothrift.view.theme.bg
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

    val editor = sharedPreferences.edit()

    val localEmail: String = sharedPreferences.getString("email", "").toString()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 2,
    )


    LaunchedEffect(Unit)

    {
        delay(2000)
        if (localEmail.isEmpty()) {
            val intent = Intent(context, HomepageActivity::class.java)
            context.startActivity(intent)
            activity?.finish() }
        //else {
//            val intent = Intent(context, DashboardActivity::class.java)
//            context.startActivity(intent)
//            activity?.finish()
//        }


   }
    Scaffold { innerPadding->
        Column(
            modifier = Modifier
                .padding(innerPadding).background(color = bg)
                .fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .clip(
                        shape = CircleShape

                    )

            )
            Spacer(
                modifier = Modifier
                    .height(30.dp)
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(90.dp)
            )

        }

    }


}
@Preview(showBackground = true)
@Composable
fun PreviewSplashScreen(){
    SplashScreenBody()
}
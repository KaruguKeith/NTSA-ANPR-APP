package com.example.ntsaanpr

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class WelcomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelcomeScreenContent()
        }
    }
}

@Composable
fun WelcomeScreenContent() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Push content to center vertically

        // Replace the NTSA text with the NTSA logo
        Image(
            painter = painterResource(id = R.drawable.ic_ntsa_icon), // Replace with your NTSA logo resource
            contentDescription = "NTSA Logo",
            modifier = Modifier
                .size(200.dp) // Adjust the size to fit your design
        )

        Spacer(modifier = Modifier.weight(1f)) // Push content towards bottom

        // Welcome button at the bottom, larger size
        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp) // Increase button height for a larger appearance
        ) {
            Text(text = "Welcome →", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp)) // Space between button and text

        // Made by and copyright text
        Text(
            text = "Made by Keith Jambo (c) 2024",
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}




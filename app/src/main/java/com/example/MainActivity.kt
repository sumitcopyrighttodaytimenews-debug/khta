package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    if (FirebaseApp.getApps(this).isEmpty()) {
       val options = FirebaseOptions.Builder()
           .setApiKey("AIzaSyBBlcGbah8RGgdLayczXbHpx7bXU-LgsWM")
           .setDatabaseUrl("https://coinapp-aba24-default-rtdb.firebaseio.com")
           .setApplicationId("1:888352378463:web:5370580834c4f9023555ea")
           .setProjectId("coinapp-aba24")
           .build()
       FirebaseApp.initializeApp(this, options)
    }

    setContent {
      MyApplicationTheme {
        KhataApp()
      }
    }
  }
}

@Composable
fun KhataApp() {
    AppNavigation()
}

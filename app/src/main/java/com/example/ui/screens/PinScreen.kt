package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MaroonPrimary

@Composable
fun PinScreen(isSetup: Boolean, onPinSuccess: () -> Unit) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    
    val title = if (isSetup) {
        if (isConfirming) "Confirm 4-Digit PIN" else "Set New 4-Digit PIN"
    } else {
        "Enter PIN to Unlock"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Wine Shop Khata",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaroonPrimary,
            modifier = Modifier.padding(bottom = 48.dp)
        )
    
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = if (isConfirming) confirmPin else pin,
            onValueChange = { 
                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                    if (isConfirming) confirmPin = it else pin = it
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center),
            modifier = Modifier.fillMaxWidth(0.6f),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (isSetup) {
                    if (!isConfirming) {
                        if (pin.length == 4) {
                            isConfirming = true
                        } else {
                            Toast.makeText(context, "Enter 4 digits", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (confirmPin == pin) {
                            sharedPrefs.edit().putString("app_pin", pin).apply()
                            onPinSuccess()
                        } else {
                            Toast.makeText(context, "PINs do not match", Toast.LENGTH_SHORT).show()
                            confirmPin = ""
                            isConfirming = false
                            pin = ""
                        }
                    }
                } else {
                    val savedPin = sharedPrefs.getString("app_pin", "")
                    if (pin == savedPin) {
                        onPinSuccess()
                    } else {
                        Toast.makeText(context, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                        pin = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.6f).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary)
        ) {
            Text("Proceed", fontSize = 18.sp, color = Color.White)
        }
    }
}

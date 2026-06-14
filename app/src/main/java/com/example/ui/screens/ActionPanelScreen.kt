package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Customer
import com.example.model.Transaction
import com.example.ui.KhataViewModel
import com.example.ui.components.AppTourOverlay
import com.example.ui.theme.GreenJama
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.RedUdhaar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionPanelScreen(
    customerId: String,
    viewModel: KhataViewModel,
    onBack: () -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    val customer = customers.find { it.id == customerId } ?: return
    
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var tourStep by remember { mutableStateOf(sharedPrefs.getInt("tour_step", 0)) }
    var quickEntryRect by remember { mutableStateOf<Rect?>(null) }
    
    var manualAmount by remember { mutableStateOf("") }
    
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    
    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this entry? Balance will be auto-adjusted.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTransaction(customerId, transactionToDelete!!.id)
                    transactionToDelete = null
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer.name, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaroonPrimary)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Customer Header (Balance)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        val isJama = customer.balance < 0
                        val color = if (customer.balance > 0) RedUdhaar else if (isJama) GreenJama else Color.Black
                        val prefix = if (isJama) "Advance " else "Udhaar "

                        Text("Current Balance", fontSize = 16.sp, color = Color.Gray)
                        Text(
                            text = "$prefix₹${"%.2f".format(Math.abs(customer.balance))}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                }
                
                // Quick Entry
                Row(
                    modifier = Modifier.fillMaxWidth().onGloballyPositioned { quickEntryRect = it.boundsInWindow() },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.addTransaction(customerId, 50.0, "1 Glass", "udhaari") },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedUdhaar)
                    ) {
                        Text("1 Glass (₹50)", color = Color.White)
                    }
                    Button(
                        onClick = { viewModel.addTransaction(customerId, 30.0, "Half Glass", "udhaari") },
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedUdhaar)
                    ) {
                        Text("Half Glass (₹30)", color = Color.White)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Manual Entry
                OutlinedTextField(
                    value = manualAmount,
                    onValueChange = { manualAmount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val amt = manualAmount.toDoubleOrNull()
                            if (amt != null && amt > 0) {
                                viewModel.addTransaction(customerId, amt, "Manual Udhaar", "udhaari")
                                manualAmount = ""
                            }
                        },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RedUdhaar)
                    ) {
                        Text("Dediya (Udhaar)", color = Color.White)
                    }
                    Button(
                        onClick = {
                            val amt = manualAmount.toDoubleOrNull()
                            if (amt != null && amt > 0) {
                                viewModel.addTransaction(customerId, amt, "Cash Jama", "jama")
                                manualAmount = ""
                            }
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenJama)
                    ) {
                        Text("Milgaya (Jama)", color = Color.White)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Passbook (Transaction History)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Timeline
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(customer.history) { trans ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = trans.time, fontSize = 12.sp, color = Color.Gray)
                                    Text(text = trans.desc, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                }
                                val color = if (trans.type == "udhaari") RedUdhaar else GreenJama
                                Text(
                                    text = "₹${"%.2f".format(trans.amt)}",
                                    color = color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                                IconButton(onClick = { transactionToDelete = trans }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            if (tourStep == 3) {
                AppTourOverlay(
                    targetRect = quickEntryRect,
                    title = "Quick Entry",
                    description = "1 Glass ya Half glass ki direct entry karein",
                    onNext = { 
                        tourStep = 4 // End of tour
                        sharedPrefs.edit().putInt("tour_step", 4).apply()
                    }
                )
            }
        }
    }
}

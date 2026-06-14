package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Customer
import com.example.ui.KhataViewModel
import com.example.ui.components.AppTourOverlay
import com.example.ui.theme.GreenJama
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.RedUdhaar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: KhataViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToCustomer: (String) -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    val filteredCustomers = customers.filter { it.name.contains(searchQuery, ignoreCase = true) }

    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    var tourStep by remember { mutableStateOf(sharedPrefs.getInt("tour_step", 0)) }
    
    var searchRect by remember { mutableStateOf<Rect?>(null) }
    var balanceRect by remember { mutableStateOf<Rect?>(null) }
    var newButtonRect by remember { mutableStateOf<Rect?>(null) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF4F6F9),
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .drawBehind {
                            val strokeWidth = 1.dp.toPx()
                            val y = size.height - strokeWidth / 2
                            drawLine(Color(0xFFE2E8F0), Offset(0f, y), Offset(size.width, y), strokeWidth)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaroonPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("W", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Text("Khata Pro Max", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaroonPrimary)
                    }
                    IconButton(onClick = {}) {
                        // Empty action simply to hold place if needed, could trigger search expand
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            ) {
                // Background bar
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .padding(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = MaroonPrimary, modifier = Modifier.size(28.dp))
                        Text("HOME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaroonPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.List, contentDescription = "Reports", tint = Color.LightGray, modifier = Modifier.size(28.dp))
                        Text("REPORTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                    }
                }

                // FAB overlapping
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFF4F6F9), CircleShape)
                            .padding(4.dp)
                            .background(MaroonPrimary, CircleShape)
                            .clickable { onNavigateToAdd() }
                            .onGloballyPositioned { newButtonRect = it.boundsInWindow() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Khata", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Text("NAYA KHATA", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Total Balance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .onGloballyPositioned { coordinates ->
                            balanceRect = coordinates.boundsInWindow()
                        },
                    colors = CardDefaults.cardColors(containerColor = MaroonPrimary),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            "KUL BAKAAYA (TOTAL BALANCE)", 
                            color = Color.White.copy(alpha = 0.7f), 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${"%.2f".format(totalOutstanding)}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("GRAHAK", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text("${customers.size}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.White.copy(alpha = 0.2f)))
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("AAJ KA UDHAAR", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text("-", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Grahak ka naam khojein", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .onGloballyPositioned { coordinates ->
                            searchRect = coordinates.boundsInWindow()
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFFE2E8F0),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )
                
                // Header List
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("RECENT CUSTOMERS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp)
                    Text("View All", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaroonPrimary, modifier = Modifier.clickable {  })
                }

                // Customer List
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredCustomers) { customer ->
                        CustomerItem(customer, onClick = { onNavigateToCustomer(customer.id) })
                    }
                }
            }
            
            // App Tour Logic for Home Screen (Steps 0, 1, 2)
            if (tourStep == 0) {
                AppTourOverlay(
                    targetRect = searchRect,
                    title = "Search Bar",
                    description = "Grahak ka naam yahan khojein",
                    onNext = { 
                        tourStep = 1
                        sharedPrefs.edit().putInt("tour_step", 1).apply()
                    }
                )
            } else if (tourStep == 1) {
                AppTourOverlay(
                    targetRect = balanceRect,
                    title = "Total Balance",
                    description = "Kul Bakaaya yahan dekhein",
                    onNext = { 
                        tourStep = 2
                        sharedPrefs.edit().putInt("tour_step", 2).apply()
                    }
                )
            } else if (tourStep == 2) {
                AppTourOverlay(
                    targetRect = newButtonRect,
                    title = "New Khata",
                    description = "Naya Khata kholne ke liye yahan dabayein",
                    onNext = { 
                        tourStep = 3 // Move to next tour logic which is inside ActionPanel
                        sharedPrefs.edit().putInt("tour_step", 3).apply()
                    }
                )
            }
        }
    }
}

@Composable
fun CustomerItem(customer: Customer, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val hash = customer.name.hashCode()

            val bgColors = listOf(Color(0xFFFFEDD5), Color(0xFFDBEAFE), Color(0xFFF3E8FF))
            val textColors = listOf(Color(0xFFC2410C), Color(0xFF1D4ED8), Color(0xFF7E22CE))
            
            val colorIndex = Math.abs(hash) % bgColors.size
            val bgColor = bgColors[colorIndex]
            val textColor = textColors[colorIndex]
            
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = customer.name.take(1).uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = customer.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                if (customer.phone.isNotEmpty()) {
                    Text(text = customer.phone, fontSize = 12.sp, color = Color(0xFF94A3B8))
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val isUdhaar = customer.balance > 0
                val isJama = customer.balance <= 0
                val color = if (isUdhaar) Color(0xFFDC2626) else if (isJama) Color(0xFF16A34A) else Color.Gray
                
                Text(
                    text = "₹${"%.2f".format(Math.abs(customer.balance))}",
                    color = color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isUdhaar) "Udhaar" else "Cleared",
                    color = color.copy(alpha=0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

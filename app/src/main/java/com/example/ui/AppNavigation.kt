package com.example.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AddCustomerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PinScreen
import com.example.ui.screens.ActionPanelScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: KhataViewModel = viewModel()
    val context = LocalContext.current
    
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val savedPin = sharedPrefs.getString("app_pin", null)
    
    val startDestination = if (savedPin == null) "pin_setup" else "pin_unlock"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("pin_setup") {
            PinScreen(
                isSetup = true,
                onPinSuccess = {
                    navController.navigate("home") {
                        popUpTo("pin_setup") { inclusive = true }
                    }
                }
            )
        }
        composable("pin_unlock") {
            PinScreen(
                isSetup = false, // Unlock
                onPinSuccess = {
                    navController.navigate("home") {
                        popUpTo("pin_unlock") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAdd = { navController.navigate("add_customer") },
                onNavigateToCustomer = { customerId ->
                    navController.navigate("action_panel/$customerId")
                }
            )
        }
        composable("add_customer") {
            AddCustomerScreen(
                onCustomerAdded = { name, phone ->
                    val id = viewModel.addCustomer(name, phone)
                    navController.popBackStack()
                    navController.navigate("action_panel/$id")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("action_panel/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")
            if (customerId != null) {
                ActionPanelScreen(
                    customerId = customerId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

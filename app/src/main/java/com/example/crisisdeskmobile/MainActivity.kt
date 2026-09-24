package com.example.crisisdeskmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crisisdeskmobile.ui.auth.AuthViewModel
import com.example.crisisdeskmobile.ui.auth.LoginScreen
import com.example.crisisdeskmobile.ui.auth.RegisterScreen
import com.example.crisisdeskmobile.ui.incident.LogIncidentScreen
import com.example.crisisdeskmobile.ui.main.MainAppScreen
import com.example.crisisdeskmobile.ui.theme.CrisisDeskMobileTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrisisDeskMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CrisisDeskAppNavHost(authViewModel = authViewModel)
                }
            }
        }
    }
}

@Composable
fun CrisisDeskAppNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    val startDestination = if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null) {
        "main"
    } else {
        "login"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    authViewModel.resetState()
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    authViewModel.resetState()
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainAppScreen(
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onNavigateToLogIncident = {
                    navController.navigate("log_incident")
                }
            )
        }

        composable("log_incident") {
            LogIncidentScreen(
                onIncidentLogged = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

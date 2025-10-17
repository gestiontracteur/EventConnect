package com.example.eventconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventconnect.data.EventDatabase
import com.example.eventconnect.ui.MainScreen
import com.example.eventconnect.ui.EventDetailScreen
import com.example.eventconnect.ui.theme.EventConnectTheme
import com.example.eventconnect.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = EventDatabase.getDatabase(this)
        val eventDao = db.eventDao()
        val themeViewModel: ThemeViewModel by viewModels()

        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            EventConnectTheme(darkTheme = isDarkTheme) {
                Surface {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        // --- Écran principal ---
                        composable("main") {
                            MainScreen(
                                eventDao = eventDao,
                                onEventClick = { eventId ->
                                    navController.navigate("details/$eventId")
                                },
                                onToggleTheme = { themeViewModel.toggleTheme() },
                                isDarkTheme = isDarkTheme
                            )
                        }

                        // --- Écran détail ---
                        composable(
                            route = "details/{eventId}",
                            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val eventId = backStackEntry.arguments?.getInt("eventId") ?: return@composable
                            EventDetailScreen(
                                eventDao = eventDao,
                                eventId = eventId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

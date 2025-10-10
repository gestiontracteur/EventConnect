package com.example.eventconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventconnect.data.EventDatabase
import com.example.eventconnect.ui.MainScreen
import com.example.eventconnect.ui.EventDetailScreen
import com.example.eventconnect.ui.theme.EventConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = EventDatabase.getDatabase(this)
        val eventDao = db.eventDao()

        setContent {
            EventConnectTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    // écran principal (liste + ajout)
                    composable("main") {
                        MainScreen(
                            eventDao = eventDao,
                            onEventClick = { eventId ->
                                navController.navigate("details/$eventId")
                            }
                        )
                    }

                    // écran détail (reçoit l'id en Int)
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
                } // fin NavHost
            } // fin EventConnectTheme
        } // fin setContent
    } // fin onCreate
} // fin MainActivity

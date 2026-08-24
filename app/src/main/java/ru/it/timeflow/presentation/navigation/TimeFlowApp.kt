package ru.it.timeflow.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.it.timeflow.presentation.analytics.AnalyticsRoute
import ru.it.timeflow.presentation.history.HistoryRoute
import ru.it.timeflow.presentation.home.HomeRoute

private sealed class Destination(
    val route: String,
    val label: String,
) {
    data object Home : Destination("home", "Сегодня")
    data object History : Destination("history", "История")
    data object Analytics : Destination("analytics", "Аналитика")
}

@Composable
fun TimeFlowApp() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination
    val destinations = listOf(Destination.Home, Destination.History, Destination.Analytics)

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    val selected = current?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(Destination.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (destination) {
                                    Destination.Home -> Icons.Default.Today
                                    Destination.History -> Icons.Default.History
                                    Destination.Analytics -> Icons.Default.Analytics
                                },
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Home.route) { HomeRoute() }
            composable(Destination.History.route) { HistoryRoute() }
            composable(Destination.Analytics.route) { AnalyticsRoute() }
        }
    }
}

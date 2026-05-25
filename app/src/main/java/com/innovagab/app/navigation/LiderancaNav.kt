package com.innovagab.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.innovagab.app.features.lideranca.DashboardScreen
import com.innovagab.app.features.lideranca.DashboardViewModel
import com.innovagab.app.features.lideranca.EstrategiasScreen
import com.innovagab.app.ui.components.AppBottomBar
import com.innovagab.app.ui.components.AppTopBar
import com.innovagab.app.ui.components.BottomNavItem

private const val DASHBOARD = "dashboard"
private const val ESTRATEGIAS = "estrategias"

@Composable
fun LiderancaNav(onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: DASHBOARD
    val dashboardViewModel: DashboardViewModel = viewModel()

    val tabs = listOf(
        BottomNavItem(DASHBOARD, "Dashboard", Icons.Default.Dashboard),
        BottomNavItem(ESTRATEGIAS, "Estratégias", Icons.Default.Flag),
    )

    val pageTitle = when (currentRoute) {
        DASHBOARD -> "Painel Executivo"
        ESTRATEGIAS -> "Estratégias"
        else -> "Liderança"
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = pageTitle,
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sair",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
        },
        bottomBar = {
            AppBottomBar(
                items = tabs,
                currentRoute = currentRoute,
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DASHBOARD,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(DASHBOARD) { DashboardScreen(viewModel = dashboardViewModel) }
            composable(ESTRATEGIAS) { EstrategiasScreen() }
        }
    }
}

package com.innovagab.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.innovagab.app.features.lideranca.CreateStrategyScreen
import com.innovagab.app.features.lideranca.DashboardScreen
import com.innovagab.app.features.lideranca.DashboardViewModel
import com.innovagab.app.features.lideranca.StrategiesScreen
import com.innovagab.app.features.lideranca.StrategiesViewModel
import com.innovagab.app.features.lideranca.StrategyDetailsScreen
import com.innovagab.app.features.recognition.GrantRecognitionScreen
import com.innovagab.app.features.recognition.LeaderboardScreen
import com.innovagab.app.features.recognition.RecognitionViewModel
import com.innovagab.app.features.recognition.UserAchievementsScreen
import com.innovagab.app.ui.components.AppBottomBar
import com.innovagab.app.ui.components.AppTopBar
import com.innovagab.app.ui.components.BottomNavItem

private const val DASHBOARD = "dashboard"
private const val ESTRATEGIAS = "estrategias"
private const val RECONHECIMENTO = "reconhecimento"
private const val STRATEGY_DETAIL = "strategy_detail/{strategyId}"
private const val CREATE_STRATEGY = "create_strategy"
private const val EDIT_STRATEGY = "edit_strategy/{strategyId}"
private const val USER_ACHIEVEMENTS = "user_achievements/{userId}"
private const val GRANT_RECOGNITION = "grant_recognition"

@Composable
fun LiderancaNav(onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: DASHBOARD

    val dashboardViewModel: DashboardViewModel = viewModel()
    val strategiesViewModel: StrategiesViewModel = viewModel()
    val recognitionViewModel: RecognitionViewModel = viewModel()

    val isOnSubscreen = currentRoute.startsWith("strategy_detail") ||
        currentRoute == CREATE_STRATEGY ||
        currentRoute.startsWith("edit_strategy") ||
        currentRoute.startsWith("user_achievements") ||
        currentRoute == GRANT_RECOGNITION

    val tabs = listOf(
        BottomNavItem(DASHBOARD, "Dashboard", Icons.Default.Dashboard),
        BottomNavItem(ESTRATEGIAS, "Estratégias", Icons.Default.Flag),
        BottomNavItem(RECONHECIMENTO, "Ranking", Icons.Default.WorkspacePremium),
    )

    val pageTitle = when {
        currentRoute == DASHBOARD -> "Painel Executivo"
        currentRoute == ESTRATEGIAS -> "Estratégias"
        currentRoute == RECONHECIMENTO -> "Reconhecimento"
        currentRoute.startsWith("strategy_detail") -> "Detalhes"
        currentRoute == CREATE_STRATEGY -> "Nova Estratégia"
        currentRoute.startsWith("edit_strategy") -> "Editar Estratégia"
        currentRoute.startsWith("user_achievements") -> "Conquistas"
        currentRoute == GRANT_RECOGNITION -> "Conceder Reconhecimento"
        else -> "Liderança"
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = pageTitle,
                onNavigateUp = if (isOnSubscreen) ({ navController.popBackStack() }) else null,
                actions = {
                    if (!isOnSubscreen) {
                        IconButton(onClick = onSignOut) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Sair",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (!isOnSubscreen) {
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
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DASHBOARD,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(DASHBOARD) {
                DashboardScreen(viewModel = dashboardViewModel)
            }
            composable(ESTRATEGIAS) {
                StrategiesScreen(
                    viewModel = strategiesViewModel,
                    canEdit = true,
                    onStrategyClick = { id -> navController.navigate("strategy_detail/$id") },
                    onCreateClick = { navController.navigate(CREATE_STRATEGY) },
                )
            }
            composable(RECONHECIMENTO) {
                LeaderboardScreen(
                    viewModel = recognitionViewModel,
                    currentUserId = recognitionViewModel.currentUserId,
                    canGrant = true,
                    onUserClick = { userId -> navController.navigate("user_achievements/$userId") },
                    onGrantClick = { navController.navigate(GRANT_RECOGNITION) },
                )
            }
            composable(
                route = STRATEGY_DETAIL,
                arguments = listOf(navArgument("strategyId") { type = NavType.StringType }),
            ) { backStack ->
                val strategyId = backStack.arguments?.getString("strategyId") ?: return@composable
                StrategyDetailsScreen(
                    strategyId = strategyId,
                    viewModel = strategiesViewModel,
                    canEdit = true,
                    onEditClick = { navController.navigate("edit_strategy/$strategyId") },
                    onDeleted = { navController.popBackStack() },
                )
            }
            composable(CREATE_STRATEGY) {
                CreateStrategyScreen(
                    viewModel = strategiesViewModel,
                    onSuccess = { navController.popBackStack() },
                )
            }
            composable(
                route = EDIT_STRATEGY,
                arguments = listOf(navArgument("strategyId") { type = NavType.StringType }),
            ) { backStack ->
                val strategyId = backStack.arguments?.getString("strategyId") ?: return@composable
                val strategy = strategiesViewModel.detailState.value.strategy
                CreateStrategyScreen(
                    viewModel = strategiesViewModel,
                    existingStrategy = strategy,
                    onSuccess = { navController.popBackStack() },
                )
            }
            composable(
                route = USER_ACHIEVEMENTS,
                arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            ) { backStack ->
                val userId = backStack.arguments?.getString("userId") ?: return@composable
                UserAchievementsScreen(userId = userId, viewModel = recognitionViewModel)
            }
            composable(GRANT_RECOGNITION) {
                GrantRecognitionScreen(
                    viewModel = recognitionViewModel,
                    onSuccess = { navController.popBackStack() },
                )
            }
        }
    }
}

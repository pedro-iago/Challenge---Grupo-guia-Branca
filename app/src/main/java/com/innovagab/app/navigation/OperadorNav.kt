package com.innovagab.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
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
import com.innovagab.app.features.home.HomeScreen
import com.innovagab.app.features.lideranca.StrategiesScreen
import com.innovagab.app.features.lideranca.StrategiesViewModel
import com.innovagab.app.features.lideranca.StrategyDetailsScreen
import com.innovagab.app.features.operador.CreateIdeaScreen
import com.innovagab.app.features.operador.IdeaDetailsScreen
import com.innovagab.app.features.operador.IdeaViewModel
import com.innovagab.app.features.operador.MyIdeasScreen
import com.innovagab.app.features.recognition.LeaderboardScreen
import com.innovagab.app.features.recognition.RecognitionViewModel
import com.innovagab.app.features.recognition.UserAchievementsScreen
import com.innovagab.app.ui.components.AppBottomBar
import com.innovagab.app.ui.components.AppTopBar
import com.innovagab.app.ui.components.BottomNavItem

private const val HOME = "home"
private const val NOVA_IDEIA = "nova_ideia"
private const val MINHAS_IDEIAS = "minhas_ideias"
private const val ESTRATEGIAS = "estrategias"
private const val RECONHECIMENTO = "reconhecimento"
private const val IDEA_DETAILS = "idea_details/{ideaId}"
private const val STRATEGY_DETAIL = "strategy_detail/{strategyId}"
private const val USER_ACHIEVEMENTS = "user_achievements/{userId}"

@Composable
fun OperadorNav(
    userName: String = "Operador",
    onSignOut: () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: HOME

    val ideaViewModel: IdeaViewModel = viewModel()
    val strategiesViewModel: StrategiesViewModel = viewModel()
    val recognitionViewModel: RecognitionViewModel = viewModel()

    val isOnDetailScreen = currentRoute.startsWith("idea_details") ||
        currentRoute.startsWith("strategy_detail") ||
        currentRoute.startsWith("user_achievements")

    val tabs = listOf(
        BottomNavItem(HOME, "Início", Icons.Default.Home),
        BottomNavItem(NOVA_IDEIA, "Ideia", Icons.Default.Lightbulb),
        BottomNavItem(MINHAS_IDEIAS, "Minhas", Icons.AutoMirrored.Filled.List),
        BottomNavItem(ESTRATEGIAS, "Estratégia", Icons.Default.Flag),
        BottomNavItem(RECONHECIMENTO, "Ranking", Icons.Default.WorkspacePremium),
    )

    val pageTitle = when {
        currentRoute == HOME -> "Início"
        currentRoute == NOVA_IDEIA -> "Nova Ideia"
        currentRoute == MINHAS_IDEIAS -> "Minhas Ideias"
        currentRoute == ESTRATEGIAS -> "Estratégias"
        currentRoute == RECONHECIMENTO -> "Reconhecimento"
        currentRoute.startsWith("idea_details") -> "Detalhes da Ideia"
        currentRoute.startsWith("strategy_detail") -> "Detalhes"
        currentRoute.startsWith("user_achievements") -> "Conquistas"
        else -> "InnovaGAB"
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = pageTitle,
                onNavigateUp = if (isOnDetailScreen) ({ navController.popBackStack() }) else null,
                actions = {
                    if (!isOnDetailScreen) {
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
            if (!isOnDetailScreen) {
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
            startDestination = HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(HOME) {
                HomeScreen(
                    userName = userName,
                    recognitionViewModel = recognitionViewModel,
                )
            }
            composable(NOVA_IDEIA) { CreateIdeaScreen(viewModel = ideaViewModel) }
            composable(MINHAS_IDEIAS) {
                MyIdeasScreen(
                    viewModel = ideaViewModel,
                    onIdeaClick = { ideaId -> navController.navigate("idea_details/$ideaId") },
                )
            }
            composable(ESTRATEGIAS) {
                StrategiesScreen(
                    viewModel = strategiesViewModel,
                    canEdit = false,
                    onStrategyClick = { id -> navController.navigate("strategy_detail/$id") },
                )
            }
            composable(RECONHECIMENTO) {
                LeaderboardScreen(
                    viewModel = recognitionViewModel,
                    currentUserId = recognitionViewModel.currentUserId,
                    canGrant = false,
                    onUserClick = { userId -> navController.navigate("user_achievements/$userId") },
                    onGrantClick = {},
                )
            }
            composable(
                route = IDEA_DETAILS,
                arguments = listOf(navArgument("ideaId") { type = NavType.StringType }),
            ) { backStack ->
                val ideaId = backStack.arguments?.getString("ideaId") ?: return@composable
                IdeaDetailsScreen(ideaId = ideaId, viewModel = ideaViewModel)
            }
            composable(
                route = STRATEGY_DETAIL,
                arguments = listOf(navArgument("strategyId") { type = NavType.StringType }),
            ) { backStack ->
                val strategyId = backStack.arguments?.getString("strategyId") ?: return@composable
                StrategyDetailsScreen(
                    strategyId = strategyId,
                    viewModel = strategiesViewModel,
                    canEdit = false,
                    onEditClick = {},
                    onDeleted = { navController.popBackStack() },
                )
            }
            composable(
                route = USER_ACHIEVEMENTS,
                arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            ) { backStack ->
                val userId = backStack.arguments?.getString("userId") ?: return@composable
                UserAchievementsScreen(userId = userId, viewModel = recognitionViewModel)
            }
        }
    }
}

package com.innovagab.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FolderOpen
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
import com.innovagab.app.features.gestor.EditProjectScreen
import com.innovagab.app.features.gestor.PipelineScreen
import com.innovagab.app.features.gestor.PipelineViewModel
import com.innovagab.app.features.gestor.ProjectDetailsScreen
import com.innovagab.app.features.gestor.ProjectViewModel
import com.innovagab.app.features.gestor.ProjectsScreen
import com.innovagab.app.features.lideranca.StrategiesScreen
import com.innovagab.app.features.lideranca.StrategiesViewModel
import com.innovagab.app.features.lideranca.StrategyDetailsScreen
import com.innovagab.app.features.recognition.LeaderboardScreen
import com.innovagab.app.features.recognition.RecognitionViewModel
import com.innovagab.app.features.recognition.UserAchievementsScreen
import com.innovagab.app.ui.components.AppBottomBar
import com.innovagab.app.ui.components.AppTopBar
import com.innovagab.app.ui.components.BottomNavItem

private const val PIPELINE = "pipeline"
private const val PROJETOS = "projetos"
private const val ESTRATEGIAS = "estrategias"
private const val RECONHECIMENTO = "reconhecimento"
private const val PROJECT_DETAIL = "project_detail/{projectId}"
private const val EDIT_PROJECT = "edit_project/{projectId}"
private const val STRATEGY_DETAIL = "strategy_detail/{strategyId}"
private const val USER_ACHIEVEMENTS = "user_achievements/{userId}"

@Composable
fun GestorNav(onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: PIPELINE

    val pipelineViewModel: PipelineViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()
    val strategiesViewModel: StrategiesViewModel = viewModel()
    val recognitionViewModel: RecognitionViewModel = viewModel()

    val isOnSubscreen = currentRoute.startsWith("project_detail") ||
        currentRoute.startsWith("edit_project") ||
        currentRoute.startsWith("strategy_detail") ||
        currentRoute.startsWith("user_achievements")

    val tabs = listOf(
        BottomNavItem(PIPELINE, "Pipeline", Icons.Default.AccountTree),
        BottomNavItem(PROJETOS, "Projetos", Icons.Default.FolderOpen),
        BottomNavItem(ESTRATEGIAS, "Estratégias", Icons.Default.Flag),
        BottomNavItem(RECONHECIMENTO, "Ranking", Icons.Default.WorkspacePremium),
    )

    val pageTitle = when {
        currentRoute == PIPELINE -> "Pipeline"
        currentRoute == PROJETOS -> "Projetos"
        currentRoute == ESTRATEGIAS -> "Estratégias"
        currentRoute == RECONHECIMENTO -> "Reconhecimento"
        currentRoute.startsWith("project_detail") -> "Detalhes do Projeto"
        currentRoute.startsWith("edit_project") -> "Editar Projeto"
        currentRoute.startsWith("strategy_detail") -> "Detalhes"
        currentRoute.startsWith("user_achievements") -> "Conquistas"
        else -> "Gestão"
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
            startDestination = PIPELINE,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(PIPELINE) {
                PipelineScreen(viewModel = pipelineViewModel)
            }
            composable(PROJETOS) {
                ProjectsScreen(
                    viewModel = projectViewModel,
                    onProjectClick = { projectId ->
                        navController.navigate("project_detail/$projectId")
                    },
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
                route = PROJECT_DETAIL,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType }),
            ) { backStack ->
                val projectId = backStack.arguments?.getString("projectId") ?: return@composable
                ProjectDetailsScreen(
                    projectId = projectId,
                    viewModel = projectViewModel,
                    onEditClick = { navController.navigate("edit_project/$projectId") },
                )
            }
            composable(
                route = EDIT_PROJECT,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType }),
            ) { backStack ->
                val projectId = backStack.arguments?.getString("projectId") ?: return@composable
                EditProjectScreen(
                    projectId = projectId,
                    viewModel = projectViewModel,
                    onSaveSuccess = { navController.popBackStack() },
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

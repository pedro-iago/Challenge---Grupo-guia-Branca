package com.innovagab.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.FolderOpen
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
import com.innovagab.app.ui.components.AppBottomBar
import com.innovagab.app.ui.components.AppTopBar
import com.innovagab.app.ui.components.BottomNavItem

private const val PIPELINE = "pipeline"
private const val PROJETOS = "projetos"
private const val PROJECT_DETAIL = "project_detail/{projectId}"
private const val EDIT_PROJECT = "edit_project/{projectId}"

@Composable
fun GestorNav(onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: PIPELINE

    val pipelineViewModel: PipelineViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()

    val isOnSubscreen = currentRoute.startsWith("project_detail") ||
        currentRoute.startsWith("edit_project")

    val tabs = listOf(
        BottomNavItem(PIPELINE, "Pipeline", Icons.Default.AccountTree),
        BottomNavItem(PROJETOS, "Projetos", Icons.Default.FolderOpen),
    )

    val pageTitle = when {
        currentRoute == PIPELINE -> "Pipeline"
        currentRoute == PROJETOS -> "Projetos"
        currentRoute.startsWith("project_detail") -> "Detalhes do Projeto"
        currentRoute.startsWith("edit_project") -> "Editar Projeto"
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
        }
    }
}

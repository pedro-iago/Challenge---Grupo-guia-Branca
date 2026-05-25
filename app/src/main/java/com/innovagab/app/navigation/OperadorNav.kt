package com.innovagab.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
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
import com.innovagab.app.features.home.HomeScreen
import com.innovagab.app.features.operador.CreateIdeaScreen
import com.innovagab.app.features.operador.IdeaDetailsScreen
import com.innovagab.app.features.operador.IdeaViewModel
import com.innovagab.app.features.operador.MyIdeasScreen
import com.innovagab.app.ui.components.AppBottomBar
import com.innovagab.app.ui.components.AppTopBar
import com.innovagab.app.ui.components.BottomNavItem

private const val HOME = "home"
private const val NOVA_IDEIA = "nova_ideia"
private const val MINHAS_IDEIAS = "minhas_ideias"
private const val IDEA_DETAILS = "idea_details/{ideaId}"

@Composable
fun OperadorNav(
    userName: String = "Operador",
    onSignOut: () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: HOME

    val ideaViewModel: IdeaViewModel = viewModel()

    val isOnDetailScreen = currentRoute.startsWith("idea_details")

    val tabs = listOf(
        BottomNavItem(HOME, "Início", Icons.Default.Home),
        BottomNavItem(NOVA_IDEIA, "Nova Ideia", Icons.Default.Lightbulb),
        BottomNavItem(MINHAS_IDEIAS, "Minhas Ideias", Icons.AutoMirrored.Filled.List),
    )

    val pageTitle = when {
        currentRoute == HOME -> "Início"
        currentRoute == NOVA_IDEIA -> "Nova Ideia"
        currentRoute == MINHAS_IDEIAS -> "Minhas Ideias"
        isOnDetailScreen -> "Detalhes da Ideia"
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
            composable(HOME) { HomeScreen(userName = userName) }
            composable(NOVA_IDEIA) { CreateIdeaScreen(viewModel = ideaViewModel) }
            composable(MINHAS_IDEIAS) {
                MyIdeasScreen(
                    viewModel = ideaViewModel,
                    onIdeaClick = { ideaId -> navController.navigate("idea_details/$ideaId") },
                )
            }
            composable(
                route = IDEA_DETAILS,
                arguments = listOf(navArgument("ideaId") { type = NavType.StringType }),
            ) { backStack ->
                val ideaId = backStack.arguments?.getString("ideaId") ?: return@composable
                IdeaDetailsScreen(ideaId = ideaId, viewModel = ideaViewModel)
            }
        }
    }
}

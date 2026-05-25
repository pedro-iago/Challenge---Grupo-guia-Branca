package com.innovagab.app.navigation

sealed class AppRoutes(val route: String) {
    data object Login : AppRoutes("login")
    data object OperadorGraph : AppRoutes("operador")
    data object GestorGraph : AppRoutes("gestor")
    data object LiderancaGraph : AppRoutes("lideranca")
}

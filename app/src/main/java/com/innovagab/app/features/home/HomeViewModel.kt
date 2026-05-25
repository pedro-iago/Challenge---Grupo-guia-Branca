package com.innovagab.app.features.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StatItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
)

data class HomeUiState(
    val userName: String = "Usuário",
    val stats: List<StatItem> = listOf(
        StatItem("Projetos Ativos", "12", Icons.Default.TrendingUp),
        StatItem("Membros", "48", Icons.Default.Group),
        StatItem("Concluídos", "7", Icons.Default.CheckCircle),
    ),
    val isLoading: Boolean = false,
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}

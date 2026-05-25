package com.innovagab.app.features.lideranca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovagab.app.data.ideas.IdeaRepository
import com.innovagab.app.data.ideas.IdeaStatus
import com.innovagab.app.data.projects.ProjectRepository
import com.innovagab.app.data.projects.ProjectStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val totalProjects: Int = 0,
    val activeProjects: Int = 0,
    val totalInvestment: Double = 0.0,
    val avgRoi: Double = 0.0,
    val estimatedSavings: Double = 0.0,
    val totalIdeas: Int = 0,
    val approvedIdeas: Int = 0,
    val pendingIdeas: Int = 0,
    val avgProgress: Float = 0f,
    val ideaStatusCounts: Map<IdeaStatus, Int> = emptyMap(),
    val projectStatusCounts: Map<ProjectStatus, Int> = emptyMap(),
)

class DashboardViewModel : ViewModel() {

    private val ideaRepository = IdeaRepository()
    private val projectRepository = ProjectRepository()

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                ideaRepository.getAllIdeasStream(),
                projectRepository.getAllProjectsStream(),
            ) { ideasResult, projectsResult ->
                val ideas = ideasResult.getOrElse { emptyList() }
                val projects = projectsResult.getOrElse { emptyList() }

                val roisWithInvestment = projects.filter { it.roi > 0.0 && it.investment > 0.0 }
                val avgRoi = if (roisWithInvestment.isEmpty()) 0.0
                else roisWithInvestment.map { it.roi }.average()

                val savings = roisWithInvestment.sumOf { it.investment * it.roi / 100.0 }
                val totalInvestment = projects.sumOf { it.investment }
                val avgProgress = if (projects.isEmpty()) 0f
                else projects.map { it.progress }.average().toFloat()

                DashboardUiState(
                    isLoading = false,
                    totalProjects = projects.size,
                    activeProjects = projects.count { it.status == ProjectStatus.ACTIVE },
                    totalInvestment = totalInvestment,
                    avgRoi = avgRoi,
                    estimatedSavings = savings,
                    totalIdeas = ideas.size,
                    approvedIdeas = ideas.count {
                        it.status == IdeaStatus.APPROVED ||
                            it.status == IdeaStatus.IN_PROGRESS ||
                            it.status == IdeaStatus.COMPLETED
                    },
                    pendingIdeas = ideas.count { it.status == IdeaStatus.PENDING },
                    avgProgress = avgProgress,
                    ideaStatusCounts = IdeaStatus.entries.associateWith { s ->
                        ideas.count { it.status == s }
                    },
                    projectStatusCounts = ProjectStatus.entries.associateWith { s ->
                        projects.count { it.status == s }
                    },
                )
            }.catch { e ->
                _state.value = DashboardUiState(isLoading = false, error = e.message)
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}

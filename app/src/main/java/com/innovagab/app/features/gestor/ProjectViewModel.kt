package com.innovagab.app.features.gestor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovagab.app.data.projects.Project
import com.innovagab.app.data.projects.ProjectRepository
import com.innovagab.app.data.projects.ProjectStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── List state ────────────────────────────────────────────────────────────────

data class ProjectsListUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val activeCount get() = projects.count { it.status == ProjectStatus.ACTIVE }
    val avgProgress get() = if (projects.isEmpty()) 0f
    else projects.map { it.progress }.average().toFloat()
    val totalInvestment get() = projects.sumOf { it.investment }
}

// ── Detail / Edit state ───────────────────────────────────────────────────────

data class ProjectDetailUiState(
    val project: Project? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class ProjectViewModel : ViewModel() {

    private val repository = ProjectRepository()

    private val _listState = MutableStateFlow(ProjectsListUiState())
    val listState: StateFlow<ProjectsListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ProjectDetailUiState())
    val detailState: StateFlow<ProjectDetailUiState> = _detailState.asStateFlow()

    private var detailJob: Job? = null

    init {
        loadAllProjects()
    }

    private fun loadAllProjects() {
        viewModelScope.launch {
            repository.getAllProjectsStream().collect { result ->
                result
                    .onSuccess { projects ->
                        _listState.update { it.copy(projects = projects, isLoading = false, error = null) }
                    }
                    .onFailure { e ->
                        _listState.update { it.copy(isLoading = false, error = e.message) }
                    }
            }
        }
    }

    fun loadProjectDetail(projectId: String) {
        detailJob?.cancel()
        _detailState.value = ProjectDetailUiState(isLoading = true)
        detailJob = viewModelScope.launch {
            repository.getProjectStream(projectId).collect { result ->
                result
                    .onSuccess { project ->
                        _detailState.update { it.copy(project = project, isLoading = false, error = null) }
                    }
                    .onFailure { e ->
                        _detailState.update { it.copy(isLoading = false, error = e.message) }
                    }
            }
        }
    }

    fun saveProjectEdit(
        projectId: String,
        progress: Float,
        roi: Double,
        investment: Double,
        deadline: String,
        status: ProjectStatus,
    ) {
        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, error = null) }
            val updates = mapOf(
                "progress" to progress.toDouble(),
                "roi" to roi,
                "investment" to investment,
                "deadline" to deadline,
                "status" to status.key,
            )
            repository.updateProject(projectId, updates)
                .onSuccess {
                    _detailState.update { it.copy(isSaving = false, saveSuccess = true) }
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun resetSaveState() {
        _detailState.update { it.copy(saveSuccess = false, error = null) }
    }

    fun dismissDetailError() {
        _detailState.update { it.copy(error = null) }
    }
}

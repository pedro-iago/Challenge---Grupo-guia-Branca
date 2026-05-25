package com.innovagab.app.features.gestor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovagab.app.data.ideas.Idea
import com.innovagab.app.data.ideas.IdeaPriority
import com.innovagab.app.data.ideas.IdeaRepository
import com.innovagab.app.data.ideas.IdeaStatus
import com.innovagab.app.data.projects.Project
import com.innovagab.app.data.projects.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── List state ────────────────────────────────────────────────────────────────

data class PipelineUiState(
    val ideas: List<Idea> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val pendingCount get() = ideas.count { it.status == IdeaStatus.PENDING }
    val approvedCount get() = ideas.count { it.status == IdeaStatus.APPROVED || it.status == IdeaStatus.IN_PROGRESS }
    val rejectedCount get() = ideas.count { it.status == IdeaStatus.REJECTED }
    val completedCount get() = ideas.count { it.status == IdeaStatus.COMPLETED }
}

// ── Review sheet state ────────────────────────────────────────────────────────

sealed interface ReviewSheet {
    data object Hidden : ReviewSheet
    data class Open(
        val idea: Idea,
        val selectedPriority: IdeaPriority = idea.priority,
        val comment: String = idea.comment,
        val isSubmitting: Boolean = false,
        val error: String? = null,
    ) : ReviewSheet
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class PipelineViewModel : ViewModel() {

    private val ideaRepository = IdeaRepository()
    private val projectRepository = ProjectRepository()

    private val _state = MutableStateFlow(PipelineUiState())
    val state: StateFlow<PipelineUiState> = _state.asStateFlow()

    private val _reviewSheet = MutableStateFlow<ReviewSheet>(ReviewSheet.Hidden)
    val reviewSheet: StateFlow<ReviewSheet> = _reviewSheet.asStateFlow()

    init {
        loadAllIdeas()
    }

    private fun loadAllIdeas() {
        viewModelScope.launch {
            ideaRepository.getAllIdeasStream().collect { result ->
                result
                    .onSuccess { ideas -> _state.update { it.copy(ideas = ideas, isLoading = false, error = null) } }
                    .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            }
        }
    }

    fun openReview(idea: Idea) {
        _reviewSheet.value = ReviewSheet.Open(idea = idea)
    }

    fun dismissReview() {
        _reviewSheet.value = ReviewSheet.Hidden
    }

    fun updateSheetPriority(priority: IdeaPriority) {
        val current = _reviewSheet.value as? ReviewSheet.Open ?: return
        _reviewSheet.value = current.copy(selectedPriority = priority)
    }

    fun updateSheetComment(comment: String) {
        val current = _reviewSheet.value as? ReviewSheet.Open ?: return
        _reviewSheet.value = current.copy(comment = comment)
    }

    fun approveIdea() {
        val sheet = _reviewSheet.value as? ReviewSheet.Open ?: return
        viewModelScope.launch {
            _reviewSheet.value = sheet.copy(isSubmitting = true, error = null)
            ideaRepository.updateIdeaReview(
                ideaId = sheet.idea.id,
                status = IdeaStatus.APPROVED,
                priority = sheet.selectedPriority,
                comment = sheet.comment,
            ).onSuccess {
                val project = Project(
                    title = sheet.idea.title,
                    description = sheet.idea.description,
                    ideaId = sheet.idea.id,
                )
                projectRepository.createProject(project)
                _reviewSheet.value = ReviewSheet.Hidden
            }.onFailure { e ->
                _reviewSheet.value = sheet.copy(isSubmitting = false, error = e.message)
            }
        }
    }

    fun rejectIdea() {
        val sheet = _reviewSheet.value as? ReviewSheet.Open ?: return
        viewModelScope.launch {
            _reviewSheet.value = sheet.copy(isSubmitting = true, error = null)
            ideaRepository.updateIdeaReview(
                ideaId = sheet.idea.id,
                status = IdeaStatus.REJECTED,
                priority = sheet.selectedPriority,
                comment = sheet.comment,
            ).onSuccess {
                _reviewSheet.value = ReviewSheet.Hidden
            }.onFailure { e ->
                _reviewSheet.value = sheet.copy(isSubmitting = false, error = e.message)
            }
        }
    }

    fun savePriority() {
        val sheet = _reviewSheet.value as? ReviewSheet.Open ?: return
        viewModelScope.launch {
            _reviewSheet.value = sheet.copy(isSubmitting = true, error = null)
            ideaRepository.updatePriority(sheet.idea.id, sheet.selectedPriority)
                .onSuccess { _reviewSheet.value = ReviewSheet.Hidden }
                .onFailure { e -> _reviewSheet.value = sheet.copy(isSubmitting = false, error = e.message) }
        }
    }

    fun dismissSheetError() {
        val current = _reviewSheet.value as? ReviewSheet.Open ?: return
        _reviewSheet.value = current.copy(error = null)
    }
}

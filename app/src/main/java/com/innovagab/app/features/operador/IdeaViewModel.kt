package com.innovagab.app.features.operador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovagab.app.data.ideas.Idea
import com.innovagab.app.data.ideas.IdeaPriority
import com.innovagab.app.data.ideas.IdeaRepository
import com.innovagab.app.data.ideas.IdeaStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── List ──────────────────────────────────────────────────────────────────────

data class IdeaListUiState(
    val ideas: List<Idea> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

// ── Detail ────────────────────────────────────────────────────────────────────

data class IdeaDetailUiState(
    val idea: Idea? = null,
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val error: String? = null,
)

// ── Create ────────────────────────────────────────────────────────────────────

sealed interface IdeaCreateUiState {
    data object Idle : IdeaCreateUiState
    data object Loading : IdeaCreateUiState
    data object Success : IdeaCreateUiState
    data class Error(val message: String) : IdeaCreateUiState
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class IdeaViewModel : ViewModel() {

    private val repository = IdeaRepository()

    private val _listState = MutableStateFlow(IdeaListUiState())
    val listState: StateFlow<IdeaListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(IdeaDetailUiState())
    val detailState: StateFlow<IdeaDetailUiState> = _detailState.asStateFlow()

    private val _createState = MutableStateFlow<IdeaCreateUiState>(IdeaCreateUiState.Idle)
    val createState: StateFlow<IdeaCreateUiState> = _createState.asStateFlow()

    private var detailJob: Job? = null

    init {
        loadUserIdeas()
    }

    private fun loadUserIdeas() {
        val userId = repository.currentUserId ?: run {
            _listState.update { it.copy(isLoading = false, error = "Usuário não autenticado") }
            return
        }
        viewModelScope.launch {
            repository.getUserIdeasStream(userId).collect { result ->
                result
                    .onSuccess { ideas -> _listState.update { it.copy(ideas = ideas, isLoading = false, error = null) } }
                    .onFailure { e -> _listState.update { it.copy(isLoading = false, error = e.message) } }
            }
        }
    }

    fun loadIdeaDetail(ideaId: String) {
        detailJob?.cancel()
        _detailState.value = IdeaDetailUiState(isLoading = true)
        detailJob = viewModelScope.launch {
            repository.getIdeaStream(ideaId).collect { result ->
                result
                    .onSuccess { idea -> _detailState.update { it.copy(idea = idea, isLoading = false, error = null) } }
                    .onFailure { e -> _detailState.update { it.copy(isLoading = false, error = e.message) } }
            }
        }
    }

    fun createIdea(
        title: String,
        description: String,
        category: String,
        impact: String,
        priority: IdeaPriority,
    ) {
        viewModelScope.launch {
            _createState.value = IdeaCreateUiState.Loading
            val idea = Idea(
                title = title.trim(),
                description = description.trim(),
                category = category,
                impact = impact,
                priority = priority,
                status = IdeaStatus.PENDING,
                createdAt = System.currentTimeMillis(),
            )
            repository.createIdea(idea)
                .onSuccess { _createState.value = IdeaCreateUiState.Success }
                .onFailure { e ->
                    _createState.value = IdeaCreateUiState.Error(
                        e.message ?: "Erro ao criar ideia. Tente novamente."
                    )
                }
        }
    }

    fun updateStatus(ideaId: String, status: IdeaStatus) {
        viewModelScope.launch {
            _detailState.update { it.copy(isUpdating = true) }
            repository.updateStatus(ideaId, status)
                .onSuccess { _detailState.update { it.copy(isUpdating = false) } }
                .onFailure { e -> _detailState.update { it.copy(isUpdating = false, error = e.message) } }
        }
    }

    fun resetCreateState() {
        _createState.value = IdeaCreateUiState.Idle
    }
}

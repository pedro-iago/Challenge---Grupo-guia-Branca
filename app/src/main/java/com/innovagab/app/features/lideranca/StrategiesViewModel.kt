package com.innovagab.app.features.lideranca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovagab.app.data.strategies.Strategy
import com.innovagab.app.data.strategies.StrategyPillar
import com.innovagab.app.data.strategies.StrategyPriority
import com.innovagab.app.data.strategies.StrategyRepository
import com.innovagab.app.data.strategies.StrategyStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class StrategiesListState(
    val isLoading: Boolean = true,
    val strategies: List<Strategy> = emptyList(),
    val error: String? = null,
)

data class StrategyDetailState(
    val isLoading: Boolean = true,
    val strategy: Strategy? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteSuccess: Boolean = false,
)

class StrategiesViewModel : ViewModel() {

    private val repository = StrategyRepository()

    private val _listState = MutableStateFlow(StrategiesListState())
    val listState: StateFlow<StrategiesListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(StrategyDetailState())
    val detailState: StateFlow<StrategyDetailState> = _detailState.asStateFlow()

    init {
        observeStrategies()
    }

    private fun observeStrategies() {
        viewModelScope.launch {
            repository.getAllStrategiesStream()
                .catch { e -> _listState.value = StrategiesListState(isLoading = false, error = e.message) }
                .collect { result ->
                    _listState.value = StrategiesListState(
                        isLoading = false,
                        strategies = result.getOrElse { emptyList() },
                        error = result.exceptionOrNull()?.message,
                    )
                }
        }
    }

    fun loadStrategyDetail(strategyId: String) {
        _detailState.value = StrategyDetailState(isLoading = true)
        viewModelScope.launch {
            repository.getStrategyStream(strategyId)
                .catch { e -> _detailState.value = StrategyDetailState(isLoading = false, error = e.message) }
                .collect { result ->
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        strategy = result.getOrNull(),
                        error = result.exceptionOrNull()?.message,
                    )
                }
        }
    }

    fun createStrategy(
        title: String,
        description: String,
        pillar: StrategyPillar,
        status: StrategyStatus,
        priority: StrategyPriority,
        cycle: String,
        deadline: String,
        owner: String,
    ) {
        _detailState.value = _detailState.value.copy(isSaving = true, error = null)
        viewModelScope.launch {
            val result = repository.createStrategy(
                Strategy(
                    title = title,
                    description = description,
                    pillar = pillar,
                    status = status,
                    priority = priority,
                    cycle = cycle,
                    deadline = deadline,
                    owner = owner,
                )
            )
            _detailState.value = _detailState.value.copy(
                isSaving = false,
                saveSuccess = result.isSuccess,
                error = result.exceptionOrNull()?.message,
            )
        }
    }

    fun updateStrategy(
        strategyId: String,
        title: String,
        description: String,
        pillar: StrategyPillar,
        status: StrategyStatus,
        priority: StrategyPriority,
        cycle: String,
        deadline: String,
        owner: String,
    ) {
        _detailState.value = _detailState.value.copy(isSaving = true, error = null)
        viewModelScope.launch {
            val result = repository.updateStrategy(
                strategyId,
                mapOf(
                    "title" to title,
                    "description" to description,
                    "pillar" to pillar.key,
                    "status" to status.key,
                    "priority" to priority.key,
                    "cycle" to cycle,
                    "deadline" to deadline,
                    "owner" to owner,
                )
            )
            _detailState.value = _detailState.value.copy(
                isSaving = false,
                saveSuccess = result.isSuccess,
                error = result.exceptionOrNull()?.message,
            )
        }
    }

    fun deleteStrategy(strategyId: String) {
        _detailState.value = _detailState.value.copy(isDeleting = true, error = null)
        viewModelScope.launch {
            val result = repository.deleteStrategy(strategyId)
            _detailState.value = _detailState.value.copy(
                isDeleting = false,
                deleteSuccess = result.isSuccess,
                error = result.exceptionOrNull()?.message,
            )
        }
    }

    fun resetDetailState() {
        _detailState.value = _detailState.value.copy(
            saveSuccess = false,
            deleteSuccess = false,
            error = null,
        )
    }
}

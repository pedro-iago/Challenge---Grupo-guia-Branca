package com.innovagab.app.features.recognition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovagab.app.data.gamification.BadgeType
import com.innovagab.app.data.gamification.GamificationRepository
import com.innovagab.app.data.gamification.Recognition
import com.innovagab.app.data.gamification.UserScore
import com.innovagab.app.data.ideas.IdeaRepository
import com.innovagab.app.data.ideas.IdeaStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class LeaderboardState(
    val isLoading: Boolean = true,
    val scores: List<UserScore> = emptyList(),
    val recentRecognitions: List<Recognition> = emptyList(),
    val error: String? = null,
)

data class GrantState(
    val isGranting: Boolean = false,
    val grantSuccess: Boolean = false,
    val error: String? = null,
    val users: List<Pair<String, String>> = emptyList(),
)

class RecognitionViewModel : ViewModel() {

    private val ideaRepository = IdeaRepository()
    private val gamificationRepository = GamificationRepository()

    val currentUserId: String? get() = gamificationRepository.currentUserId

    private val _leaderboardState = MutableStateFlow(LeaderboardState())
    val leaderboardState: StateFlow<LeaderboardState> = _leaderboardState.asStateFlow()

    private val _grantState = MutableStateFlow(GrantState())
    val grantState: StateFlow<GrantState> = _grantState.asStateFlow()

    init {
        observeLeaderboard()
        observeUsers()
    }

    private fun observeLeaderboard() {
        viewModelScope.launch {
            combine(
                ideaRepository.getAllIdeasStream(),
                gamificationRepository.getAllUsersStream(),
                gamificationRepository.getAllRecognitionsStream(),
            ) { ideasResult, usersResult, recognitionsResult ->
                val ideas = ideasResult.getOrElse { emptyList() }
                val users = usersResult.getOrElse { emptyList() }
                val recognitions = recognitionsResult.getOrElse { emptyList() }

                val recognitionsByUser = recognitions.groupBy { it.userId }
                val ideasByUser = ideas.groupBy { it.authorId }
                val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000

                val scores = users.map { (userId, userName) ->
                    val userIdeas = ideasByUser[userId] ?: emptyList()
                    val approved = userIdeas.count {
                        it.status == IdeaStatus.APPROVED ||
                            it.status == IdeaStatus.IN_PROGRESS ||
                            it.status == IdeaStatus.COMPLETED
                    }
                    val completed = userIdeas.count { it.status == IdeaStatus.COMPLETED }

                    val points = userIdeas.sumOf { idea ->
                        when (idea.status) {
                            IdeaStatus.PENDING -> 5L
                            IdeaStatus.APPROVED -> 30L
                            IdeaStatus.IN_PROGRESS -> 40L
                            IdeaStatus.COMPLETED -> 60L
                            IdeaStatus.REJECTED -> 2L
                        }
                    }.toInt()

                    val autoBadges = buildSet<BadgeType> {
                        if (userIdeas.isNotEmpty()) add(BadgeType.PIONEIRO)
                        if (userIdeas.size >= 5) add(BadgeType.INOVADOR)
                        if (approved >= 2) add(BadgeType.SOLUCIONADOR)
                        if (completed >= 1) add(BadgeType.TRANSFORMADOR)
                        if (userIdeas.any { it.createdAt >= thirtyDaysAgo }) add(BadgeType.COLABORADOR)
                    }

                    val manualBadges = recognitionsByUser[userId]
                        ?.mapNotNull { it.badge }
                        ?.toSet()
                        ?: emptySet()

                    UserScore(
                        userId = userId,
                        userName = userName,
                        points = points,
                        totalIdeas = userIdeas.size,
                        approvedIdeas = approved,
                        completedIdeas = completed,
                        autoBadges = autoBadges,
                        manualBadges = manualBadges,
                    )
                }.sortedByDescending { it.points }

                LeaderboardState(
                    isLoading = false,
                    scores = scores,
                    recentRecognitions = recognitions,
                )
            }
            .catch { e -> _leaderboardState.value = LeaderboardState(isLoading = false, error = e.message) }
            .collect { _leaderboardState.value = it }
        }
    }

    private fun observeUsers() {
        viewModelScope.launch {
            gamificationRepository.getAllUsersStream()
                .catch { }
                .collect { result ->
                    _grantState.value = _grantState.value.copy(
                        users = result.getOrElse { emptyList() }
                            .sortedBy { it.second },
                    )
                }
        }
    }

    fun grantRecognition(userId: String, userName: String, badge: BadgeType, note: String) {
        _grantState.value = _grantState.value.copy(isGranting = true, error = null)
        viewModelScope.launch {
            val result = gamificationRepository.grantRecognition(userId, userName, badge, note)
            _grantState.value = _grantState.value.copy(
                isGranting = false,
                grantSuccess = result.isSuccess,
                error = result.exceptionOrNull()?.message,
            )
        }
    }

    fun revokeRecognition(recognitionId: String) {
        viewModelScope.launch {
            gamificationRepository.revokeRecognition(recognitionId)
        }
    }

    fun resetGrantState() {
        _grantState.value = _grantState.value.copy(grantSuccess = false, error = null)
    }
}

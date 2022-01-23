package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.UserHistory

data class UserHistoryDto(
    val currentTime: Int
)


fun UserHistoryDto.toUserHistory(): UserHistory {
    return UserHistory(
        currentTime = currentTime,
    )
}
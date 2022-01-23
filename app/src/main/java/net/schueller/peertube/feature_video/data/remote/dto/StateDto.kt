package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.State

const val PUBLISHED = 1
const val TO_TRANSCODE = 2
const val TO_IMPORT = 3
const val WAITING_FOR_LIVE = 4
const val LIVE_ENDED = 5

data class StateDto(
    val id: Int,
    val label: String
)

fun StateDto.toState(): State {
    return State(
        id = id,
        label = label
    )
}
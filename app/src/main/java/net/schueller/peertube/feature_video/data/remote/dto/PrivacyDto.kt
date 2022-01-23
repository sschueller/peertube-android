package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Privacy

data class PrivacyDto(
    var id: Int,
    var label: String
)

fun PrivacyDto.toPrivacy(): Privacy {
    return Privacy(
        id = id,
        label = label
    )
}
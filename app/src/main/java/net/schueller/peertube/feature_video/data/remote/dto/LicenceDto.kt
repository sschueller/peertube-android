package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Licence

data class LicenceDto(
    var id: Int?,
    var label: String
)

fun LicenceDto.toLicence(): Licence {
    return Licence(
        id = id,
        label = label
    )
}
package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Avatar
import java.util.*

data class AvatarDto(
        var path: String,
        var createdAt: Date,
        var updatedAt: Date
)

fun AvatarDto.toAvatar(): Avatar {
        return Avatar(
                path = path
        )
}

package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Rating

data class RatingDto (
    val videoId: Int,
    val rating: String
)

fun RatingDto.toRating(): Rating {
    return Rating(
        videoId = videoId,
        rating = rating
    )
}
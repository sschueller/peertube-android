package net.schueller.peertube.feature_video.domain.model

const val RATING_NONE = "none"
const val RATING_LIKE = "like"
const val RATING_DISLIKE = "dislike"

data class Rating (
    val videoId: Int,
    val rating: String
)
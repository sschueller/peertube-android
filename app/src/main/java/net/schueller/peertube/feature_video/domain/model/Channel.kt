package net.schueller.peertube.feature_video.domain.model


data class Channel (
    val id: Int,
    val url: String,
    val name: String,
    val avatar: Avatar?,
    val displayName: String,
    val host: String
)
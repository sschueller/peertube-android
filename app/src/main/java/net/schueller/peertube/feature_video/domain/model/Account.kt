package net.schueller.peertube.feature_video.domain.model

data class Account(
    var id: Int,
    var url: String,
    var uuid: String?,
    var name: String,
    var host: String,
    var followingCount: Int,
    var followersCount: Int,
    var avatar: Avatar?,
    var displayName: String,
    var description: String?
)
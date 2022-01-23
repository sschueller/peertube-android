package net.schueller.peertube.feature_video.domain.model

data class SteamingPlaylist(

    val id: Int,
    val type: Int,
    val playlistUrl: String,
    val segmentsSha256Url: String,
    val redundancies: List<Redundancy>,
    val files: List<File>
)


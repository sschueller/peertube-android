package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.SteamingPlaylist

data class SteamingPlaylistDto(

    val id: Int,
    val type: Int,
    val playlistUrl: String,
    val segmentsSha256Url: String,
    val redundancies: List<RedundancyDto>,
    val files: List<FileDto>
)

fun SteamingPlaylistDto.toSteamingPlaylist(): SteamingPlaylist {
    return SteamingPlaylist(
        id = id,
        type = type,
        playlistUrl = playlistUrl,
        segmentsSha256Url = segmentsSha256Url,
        redundancies = redundancies.map { it.toRedundancy() },
        files = files.map { it.toFile() }
    )
}
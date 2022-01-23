package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.File

data class FileDto (
    var id: Int,
    var fileDownloadUrl: String,
    var fps: Int,
    var resolution: ResolutionDto,
    var magnetUri: String,
    var size: Int,
    var torrentUrl: String,
    var torrentDownloadUrl: String,
    var fileUrl: String
)

fun FileDto.toFile(): File {
    return File(
        id = id,
        fileDownloadUrl = fileDownloadUrl,
        fps = fps,
        resolution = resolution.toResolution(),
        magnetUri = magnetUri,
        size = size,
        torrentUrl = torrentUrl,
        torrentDownloadUrl = torrentDownloadUrl,
        fileUrl = fileUrl
    )
}
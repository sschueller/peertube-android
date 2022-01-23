package net.schueller.peertube.feature_video.domain.model

data class File (
    var id: Int,
    var fileDownloadUrl: String,
    var fps: Int,
    var resolution: Resolution,
    var magnetUri: String,
    var size: Int,
    var torrentUrl: String,
    var torrentDownloadUrl: String,
    var fileUrl: String
)
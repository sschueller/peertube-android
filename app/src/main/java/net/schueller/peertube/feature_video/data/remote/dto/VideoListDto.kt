package net.schueller.peertube.feature_video.data.remote.dto

import com.google.gson.annotations.SerializedName
import net.schueller.peertube.feature_video.domain.model.Video

data class VideoListDto(
    @SerializedName("data")
    val videos: List<VideoDto>? = emptyList()
)

fun VideoListDto.toVideoList(): List<Video> {

    val newList = mutableListOf<Video>()
    videos?.forEach { vid ->
        newList += Video(
            account = vid.account?.toAccount(),
            blacklisted = vid.blacklisted,
            blacklistedReason = vid.blacklistedReason,
            category = vid.category?.toCategory(),
            channel = vid.channel?.toChannel(),
            commentsEnabled = vid.commentsEnabled,
            createdAt = vid.createdAt,
            description = vid.description,
            descriptionPath = vid.descriptionPath,
            dislikes = vid.dislikes,
            downloadEnabled = vid.downloadEnabled,
            duration = vid.duration,
            embedPath = vid.embedPath,
            files = vid.files?.map{ it.toFile() },
            id = vid.id,
            isLive = vid.isLive,
            isLocal = vid.isLocal,
            language = vid.language?.toLanguage(),
            licence = vid.licence?.toLicence(),
            likes = vid.likes,
            name = vid.name,
            nsfw = vid.nsfw,
            originallyPublishedAt = vid.originallyPublishedAt,
            previewPath = vid.previewPath,
            privacy = vid.privacy?.toPrivacy(),
            publishedAt = vid.publishedAt,
            scheduledUpdate = vid.scheduledUpdate?.toScheduledUpdate(),
            shortUUID = vid.shortUUID,
            state = vid.state?.toState(),
            streamingPlaylists = vid.streamingPlaylists?.map { it.toSteamingPlaylist() },
            support = vid.support,
            tags = vid.tags,
            thumbnailPath = vid.thumbnailPath,
            trackerUrls = vid.trackerUrls,
            updatedAt = vid.updatedAt,
            userHistory = vid.userHistory?.toUserHistory(),
            uuid = vid.uuid,
            views = vid.views,
            waitTranscoding = vid.waitTranscoding
        )
    }
    return newList
}

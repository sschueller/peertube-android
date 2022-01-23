package net.schueller.peertube.feature_video.data.remote.dto

import com.google.gson.annotations.SerializedName
import net.schueller.peertube.feature_video.domain.model.Channel
import java.util.*

data class ChannelDto(
    val id: Int,
    val url: String,
    val uuid: String,
    val name: String,
    val host: String,
    val followingCount: Int,
    val followersCount: Int,
    @SerializedName("avatar")
    val avatarDto: AvatarDto?,
    val createdAt: Date,
    val updatedAt: Date,
    val displayName: String,
    val description: String,
    val support: String,
    val local: Boolean
)

fun ChannelDto.toChannel(): Channel {
    return Channel(
        id = id,
        url = url,
        name = name,
        displayName = displayName,
        avatar = avatarDto?.toAvatar(),
        host = host
    )
}
package net.schueller.peertube.feature_video.data.remote.dto

import com.google.gson.annotations.SerializedName
import net.schueller.peertube.feature_video.domain.model.Account
import java.util.Date

class AccountDto(
    var id: Int,
    var url: String,
    var uuid: String?,
    var name: String,
    var host: String,
    var followingCount: Int,
    var followersCount: Int,
    @SerializedName("avatar")
    var avatarDto: AvatarDto?,
    var createdAt: Date,
    var updatedAt: Date,
    var displayName: String,
    var description: String?
)

fun AccountDto.toAccount(): Account {
    return Account(
        id = id,
        url = url,
        uuid = uuid,
        name = name,
        host = host,
        followingCount = followingCount,
        followersCount = followersCount,
        avatar = avatarDto?.toAvatar(),
        displayName = displayName,
        description = description
    )
}
package net.schueller.peertube.feature_server_address.data.data_source.remote.dto

import com.google.gson.annotations.SerializedName
import net.schueller.peertube.feature_server_address.domain.model.Server

data class ServerListDto (
    @SerializedName("data")
    val servers: List<ServerDto>? = emptyList()
)


fun ServerListDto.toServerList(): List<Server> {

    val newList = mutableListOf<Server>()
    servers?.forEach { serv ->
        newList += Server(
            id = serv.id,
            host = serv.host,
            name = serv.name,
            shortDescription = serv.shortDescription,
            version = serv.version,
            signupAllowed = serv.signupAllowed,
            userVideoQuota = serv.userVideoQuota,
            liveEnabled = serv.liveEnabled,
            category = serv.category?.toCategory(),
            languages = serv.languages,
            autoBlacklistUserVideosEnabled = serv.autoBlacklistUserVideosEnabled,
            defaultNSFWPolicy = serv.defaultNSFWPolicy,
            isNSFW = serv.isNSFW,
            totalUsers = serv.totalUsers,
            totalVideos = serv.totalVideos,
            totalLocalVideos = serv.totalLocalVideos,
            totalInstanceFollowers = serv.totalInstanceFollowers,
            totalInstanceFollowing = serv.totalInstanceFollowing,
            supportsIPv6 = serv.supportsIPv6,
            country = serv.country,
            health = serv.health,
            createdAt = serv.createdAt
        )
    }
    return newList

}

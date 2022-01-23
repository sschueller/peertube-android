package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Me
import java.util.*

data class MeDto(
    val account: AccountDto?,
    val autoPlayNextVideo: Boolean,
    val autoPlayNextVideoPlaylist: Boolean,
    val autoPlayVideo: Boolean,
    val blocked: Boolean,
    val blockedReason: String?,
    val createdAt: String?,
    val email: String?,
    val emailVerified: Boolean,
    val id: Int,
    val pluginAuth: String?,
    val lastLoginDate: Date?,
    val noInstanceConfigWarningModal: Boolean,
    val noAccountSetupWarningModal: Boolean,
    val noWelcomeModal: Boolean,
    val nsfwPolicy: String?,
    val role: Int,
    val roleLabel: String?,
    val theme: String?,
    val username: String?,
    val videoChannels: List<ChannelDto>?,
    val videoQuota: Int,
    val videoQuotaDaily: Int,
    val p2pEnabled: Boolean,
)

fun MeDto.toMe(): Me {
    return Me(
        account = account?.toAccount(),
        autoPlayNextVideo = autoPlayNextVideo,
        autoPlayNextVideoPlaylist = autoPlayNextVideoPlaylist,
        autoPlayVideo = autoPlayVideo,
        blocked = blocked,
        blockedReason = blockedReason,
        createdAt = createdAt,
        email = email,
        emailVerified = emailVerified,
        id = id,
        pluginAuth = pluginAuth,
        lastLoginDate = lastLoginDate,
        noInstanceConfigWarningModal = noInstanceConfigWarningModal,
        noAccountSetupWarningModal = noAccountSetupWarningModal,
        noWelcomeModal = noWelcomeModal,
        nsfwPolicy = nsfwPolicy,
        role = role,
        roleLabel = roleLabel,
        theme = theme,
        username = username,
        videoChannels = videoChannels?.map { it.toChannel() },
        videoQuota = videoQuota,
        videoQuotaDaily = videoQuotaDaily,
        p2pEnabled = p2pEnabled,
    )
}
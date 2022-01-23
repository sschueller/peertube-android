package net.schueller.peertube.feature_video.domain.model

import java.util.*

data class Me(
    val account: Account?,
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
    val videoChannels: List<Channel>?,
    val videoQuota: Int,
    val videoQuotaDaily: Int,
    val p2pEnabled: Boolean,
)

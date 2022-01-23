package net.schueller.peertube.feature_video.presentation.video_list.components

import net.schueller.peertube.R
import net.schueller.peertube.feature_video.presentation.video_list.*

sealed class BottomBarItems(
    var route: String,
    var icon: Int,
    var title: String,
    var set: String
) {
    object Discover : BottomBarItems(
        "video_list_screen",
        R.drawable.ic_globe,
        "Discover",
        SET_DISCOVER
    )

    object Trending : BottomBarItems(
        "video_list_screen",
        R.drawable.ic_trending_up,
        "Trending",
        SET_TRENDING
        )

    object Recent : BottomBarItems(
        "video_list_screen",
        R.drawable.ic_plus_circle,
        "Recent",
        SET_RECENT
    )

    object Local : BottomBarItems(
        "video_list_screen",
        R.drawable.ic_local,
        "Local",
        SET_LOCAL
    )

    object Subscriptions : BottomBarItems(
        "settings_screen",
        R.drawable.ic_subscriptions,
        "Subscriptions",
        SET_SUBSCRIPTIONS
    )
}

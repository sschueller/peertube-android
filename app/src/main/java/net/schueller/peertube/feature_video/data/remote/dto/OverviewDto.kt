package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Overview

data class OverviewDto(
    val categories: List<CategoryVideoDto>?,
    val channels: List<ChannelVideoDto>?,
    val tags: List<TagVideoDto>?
)

fun OverviewDto.toOverview(): List<Overview> {

    val newList = mutableListOf<Overview>()

    newList += Overview(
        categories = categories?.map { it.toCategoryVideo() },
        channels = channels?.map { it.toChannelVideo() },
        tags = tags?.map { it.toTagVideo() }
    )

    return newList

}
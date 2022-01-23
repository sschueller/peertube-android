package net.schueller.peertube.feature_video.domain.repository

import net.schueller.peertube.feature_video.domain.model.*
import okhttp3.ResponseBody

interface VideoRepository {

    suspend fun getVideos(start: Int,count: Int,sort: String?,nsfw: String?,filter: String?,languages: Set<String?>?): List<Video>

    suspend fun getOverviewVideos(page: Int): List<Overview>

    suspend fun getVideoByUuid(uuid: String): Video

    suspend fun getVideoDescriptionByUuid(uuid: String): Description

    suspend fun rateVideo(id: Int, upVote: Boolean): ResponseBody

    suspend fun getVideoRating(id: Int): Rating

    suspend fun getAccountVideoPlaylists(accountName: String, start: Int, count: Int, sort: String?): List<Video>

    suspend fun getMe(): Me
}
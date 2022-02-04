package net.schueller.peertube.feature_video.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.schueller.peertube.feature_video.data.remote.PeerTubeApi
import net.schueller.peertube.feature_video.data.remote.dto.*
import net.schueller.peertube.feature_video.domain.model.*
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val api: PeerTubeApi
) : VideoRepository {

    override suspend fun getVideos(start: Int, count: Int, sort: String?,nsfw: String?, filter: String?, languages: Set<String?>?): List<Video> {
        return api.getVideos(start, count, sort, nsfw, filter, languages).toVideoList()
    }

    override suspend fun searchVideos(start: Int, count: Int, sort: String?,nsfw: String?, searchQuery: String?, filter: String?, languages: Set<String?>?): List<Video> {
        return api.searchVideos(start, count, sort, nsfw, searchQuery, filter, languages).toVideoList()
    }

    override suspend fun getVideoByUuid(uuid: String): Video {
        return api.getVideo(uuid).toVideo()
    }

    override suspend fun getVideoDescriptionByUuid(uuid: String): Description {
        return api.getVideoFullDescription(uuid).toDescription()
    }

    override suspend fun rateVideo(id: Int, upVote: Boolean) {

        val rating = if (upVote) {
            RATING_LIKE
        } else {
            RATING_DISLIKE
        }
        val jsonObject = JSONObject()
        jsonObject.put("rating", rating)
        val jsonObjectString = jsonObject.toString()

        api.rateVideo(id, jsonObjectString.toRequestBody("application/json".toMediaType()))
    }

    override suspend fun getVideoRating(id: Int): Rating {
        return api.getVideoRating(id = id).toRating()
    }

    override suspend fun getAccountVideoPlaylists(accountName: String, start: Int, count: Int, sort: String?): List<Video> {
        return api.getAccountVideoPlaylists(accountName, start, count, sort).toVideoList()
    }

    override suspend fun getOverviewVideos(page: Int): List<Overview> {
        return api.getOverviewVideos(page).toOverview()
    }

    override suspend fun getMe(): Me {
        return api.getMe().toMe()
    }

}
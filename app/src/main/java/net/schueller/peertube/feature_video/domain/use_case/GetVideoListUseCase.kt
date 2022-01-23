package net.schueller.peertube.feature_video.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.schueller.peertube.common.Resource
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetVideoListUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    operator fun invoke(start: Int,count: Int,sort: String?,nsfw: String?,filter: String?,languages: Set<String?>?): Flow<Resource<List<Video>>> = flow {
        try {
            emit(Resource.Loading<List<Video>>())
            val videos = repository.getVideos(start, count, sort, nsfw, filter, languages)
            emit(Resource.Success<List<Video>>(videos))
        } catch(e: HttpException) {
            emit(Resource.Error<List<Video>>(e.localizedMessage ?: "An unexpected error occurred"))
        } catch(e: IOException) {
            emit(Resource.Error<List<Video>>("Couldn't reach server. Check your internet connection."))
        }
    }

}
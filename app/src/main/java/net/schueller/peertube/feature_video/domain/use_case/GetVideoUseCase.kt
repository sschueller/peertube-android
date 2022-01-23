package net.schueller.peertube.feature_video.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.schueller.peertube.common.Resource
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetVideoUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    operator fun invoke(uuid: String): Flow<Resource<Video>> = flow {
        try {
            emit(Resource.Loading<Video>())
            val videos = repository.getVideoByUuid(uuid)
            emit(Resource.Success<Video>(videos))
        } catch(e: HttpException) {
            emit(Resource.Error<Video>(e.localizedMessage ?: "An unexpected error occurred"))
        } catch(e: IOException) {
            emit(Resource.Error<Video>("Couldn't reach server. Check your internet connection."))
        }
    }

}
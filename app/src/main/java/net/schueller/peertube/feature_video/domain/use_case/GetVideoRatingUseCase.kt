package net.schueller.peertube.feature_video.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.schueller.peertube.common.Resource
import net.schueller.peertube.feature_video.domain.model.Rating
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import retrofit2.HttpException
import java.io.IOException

import javax.inject.Inject

class GetVideoRatingUseCase @Inject constructor(
    private val repository: VideoRepository
){
    operator fun invoke(id: Int): Flow<Resource<Rating>> = flow {
        try {
            emit(Resource.Loading<Rating>())
            val rating = repository.getVideoRating(id)
            emit(Resource.Success<Rating>(rating))
        } catch(e: HttpException) {
            emit(Resource.Error<Rating>(e.localizedMessage ?: "An unexpected error occurred"))
        } catch(e: IOException) {
            emit(Resource.Error<Rating>("Couldn't reach server. Check your internet connection."))
        }
    }
}
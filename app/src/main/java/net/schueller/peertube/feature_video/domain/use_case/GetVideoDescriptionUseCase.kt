package net.schueller.peertube.feature_video.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.schueller.peertube.common.Resource
import net.schueller.peertube.feature_video.domain.model.Description
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetVideoDescriptionUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    operator fun invoke(uuid: String): Flow<Resource<Description>> = flow {
        try {
            emit(Resource.Loading<Description>())
            val description = repository.getVideoDescriptionByUuid(uuid)
            emit(Resource.Success<Description>(description))
        } catch(e: HttpException) {
            emit(Resource.Error<Description>(e.localizedMessage ?: "An unexpected error occurred"))
        } catch(e: IOException) {
            emit(Resource.Error<Description>("Couldn't reach server. Check your internet connection."))
        }
    }

}
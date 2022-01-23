package net.schueller.peertube.feature_video.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.schueller.peertube.common.Resource
import net.schueller.peertube.feature_video.data.remote.auth.Session
import net.schueller.peertube.feature_video.domain.model.Me
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val repository: VideoRepository,
    private val session: Session,
) {
    operator fun invoke(): Flow<Resource<Me>> = flow {
        if (session.isLoggedIn()) {
            try {
                emit(Resource.Loading<Me>())
                val me = repository.getMe()
                emit(Resource.Success<Me>(me))
            } catch(e: HttpException) {
                emit(Resource.Error<Me>(e.localizedMessage ?: "An unexpected error occurred"))
            } catch(e: IOException) {
                emit(Resource.Error<Me>("Couldn't reach server. Check your internet connection."))
            }
        } else {
            emit(Resource.Error<Me>("Not logged in"))
        }
    }

}


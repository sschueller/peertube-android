package net.schueller.peertube.feature_video.domain.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.schueller.peertube.common.Constants.VIDEOS_API_PAGE_SIZE
import net.schueller.peertube.common.Constants.VIDEOS_API_START_INDEX
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import retrofit2.HttpException
import java.io.IOException

class VideoPagingSource (
    private val repository: VideoRepository,
    private val sort: String?,
    private val nsfw: String?,
    private val filter: String?,
    private val languages: Set<String?>?
) : PagingSource<Int, Video>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        val position = params.key ?: VIDEOS_API_START_INDEX

        val offset = if (params.key != null) ((position) * VIDEOS_API_PAGE_SIZE) else VIDEOS_API_START_INDEX

        return try {
            val videos = repository.getVideos(offset, params.loadSize, sort, nsfw, filter, languages)

            val nextKey = if (videos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / VIDEOS_API_PAGE_SIZE)
            }
            LoadResult.Page(
                data = videos,
                prevKey = if (position == VIDEOS_API_START_INDEX) null else position - 1,
                nextKey = nextKey
            )

        } catch (exception: HttpException) {
            Log.v("video1", exception.localizedMessage ?: "An unexpected error occurred")
            return LoadResult.Error(exception)
        } catch (exception: IOException) {
            Log.v("video1", "Couldn't reach server. Check your internet connection.")
            return LoadResult.Error(exception)
        }
    }
    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        Log.v("video1", "getRefreshKey")
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
        return 0
    }


}

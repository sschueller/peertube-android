package net.schueller.peertube.feature_video.domain.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.schueller.peertube.common.Constants.VIDEOS_API_PAGE_SIZE
import net.schueller.peertube.common.Constants.VIDEOS_API_START_INDEX
import net.schueller.peertube.feature_video.domain.model.Overview
import net.schueller.peertube.feature_video.domain.model.Video
import net.schueller.peertube.feature_video.domain.repository.VideoRepository
import retrofit2.HttpException
import java.io.IOException

class ExplorePagingSource (
    private val repository: VideoRepository
) : PagingSource<Int, Overview>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Overview> {
        // Retrofit calls that return the body type throw either IOException for network
        // failures, or HttpException for any non-2xx HTTP status codes. This code reports all
        // errors to the UI, but you can inspect/wrap the exceptions to provide more context.
        return try {
            // Key may be null during a refresh, if no explicit key is passed into Pager
            // construction. Use 0 as default, because our API is indexed started at index 0
            val pageNumber = params.key ?: 1

            // Suspending network load via Retrofit. This doesn't need to be wrapped in a
            // withContext(Dispatcher.IO) { ... } block since Retrofit's Coroutine
            // CallAdapter dispatches on a worker thread.
            val response = repository.getOverviewVideos(pageNumber)

            // Since 1 is the lowest page number, return null to signify no more pages should
            // be loaded before it.
            val prevKey = if (pageNumber > 1) pageNumber - 1 else null

            // This API defines that it's out of data when a page returns empty. When out of
            // data, we return `null` to signify no more pages should be loaded
            val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
            LoadResult.Page(
                data = response,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }

    }
    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, Overview>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}

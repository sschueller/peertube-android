package net.schueller.peertube.feature_server_address.domain.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.schueller.peertube.common.Constants.SERVERS_API_PAGE_SIZE
import net.schueller.peertube.common.Constants.SERVERS_API_START_INDEX
import net.schueller.peertube.feature_server_address.domain.model.Server
import net.schueller.peertube.feature_server_address.domain.repository.ServerRepository
import retrofit2.HttpException
import java.io.IOException

class ServerPagingSource (
    private val repository: ServerRepository,
    private val search: String
) : PagingSource<Int, Server>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Server> {
        val position = params.key ?: SERVERS_API_START_INDEX

        val offset = if (params.key != null) ((position) * SERVERS_API_PAGE_SIZE) else SERVERS_API_START_INDEX

        return try {
            val servers = repository.getServers(offset, params.loadSize, search)

            val nextKey = if (servers.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / SERVERS_API_PAGE_SIZE)
            }
            LoadResult.Page(
                data = servers,
                prevKey = if (position == SERVERS_API_START_INDEX) null else position - 1,
                nextKey = nextKey
            )

        } catch (exception: HttpException) {
            Log.v("servers", exception.localizedMessage ?: "An unexpected error occurred")
            return LoadResult.Error(exception)
        } catch (exception: IOException) {
            Log.v("servers", "Couldn't reach server. Check your internet connection.")
            return LoadResult.Error(exception)
        }
    }
    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, Server>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}

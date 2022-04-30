package net.schueller.peertube.feature_video.presentation.video.player

import android.content.Context
import android.view.LayoutInflater
import androidx.core.util.Pools
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import net.schueller.peertube.R

object PlayerViewPool {
    var currentPlayerView: StyledPlayerView? = null

    private val playerViewPool = Pools.SimplePool<StyledPlayerView>(2)

    fun get(context: Context): StyledPlayerView {
        return playerViewPool.acquire() ?: createPlayerView(context)
    }

    fun release(player: StyledPlayerView) {
        playerViewPool.release(player)
    }

    private fun createPlayerView(context: Context): StyledPlayerView {
        return (LayoutInflater.from(context).inflate(R.layout.exoplayer_texture_view, null, false) as StyledPlayerView)
    }
}
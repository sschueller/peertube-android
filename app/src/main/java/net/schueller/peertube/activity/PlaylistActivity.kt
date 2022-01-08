/*
 * Copyright (C) 2020 Stefan Schüller <sschueller@techdroid.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import net.schueller.peertube.R
import net.schueller.peertube.adapter.PlaylistAdapter
import net.schueller.peertube.database.Video
import net.schueller.peertube.database.VideoViewModel
import net.schueller.peertube.databinding.ActivityPlaylistBinding

class PlaylistActivity : CommonActivity() {

    private val TAG = "PlaylistAct"

    private val mVideoViewModel: VideoViewModel by viewModels()

    private lateinit var mBinding: ActivityPlaylistBinding

    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(mBinding.toolBarServerAddressBook)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        }

        showServers()
    }

    private fun onVideoClick(video: Video) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
    }

    private fun showServers() {
        val adapter = PlaylistAdapter(mutableListOf(), { onVideoClick(it) }).also {
            mBinding.serverListRecyclerview.adapter = it
        }

        // Delete items on swipe
        val helper = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        AlertDialog.Builder(this@PlaylistActivity)
                                .setTitle("Remove Video")
                                .setMessage("Are you sure you want to remove this video from playlist?")
                                .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                                    val position = viewHolder.bindingAdapterPosition
                                    val video = adapter.getVideoAtPosition(position)
                                    // Delete the video
                                    mVideoViewModel.delete(video)
                                }
                                .setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int -> adapter.notifyItemChanged(viewHolder.bindingAdapterPosition) }
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show()
                    }
                })
        helper.attachToRecyclerView(mBinding.serverListRecyclerview)

        // Update the cached copy of the words in the adapter.
        mVideoViewModel.allVideos.observe(this, { videos: List<Video> ->
            adapter.setVideos(videos)
        })
    }

    companion object
}
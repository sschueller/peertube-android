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
package net.schueller.peertube.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.schueller.peertube.database.Video
import net.schueller.peertube.databinding.RowPlaylistBinding

class PlaylistAdapter(private val mVideos: MutableList<Video>, private val onClick: (Video) -> Unit) : RecyclerView.Adapter<PlaylistAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {

        val binding = RowPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(mVideos[position])
    }

    fun setVideos(videos: List<Video>) {
        mVideos.clear()
        mVideos.addAll(videos)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mVideos.size
    }

    inner class VideoViewHolder(private val binding: RowPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Video) {

            binding.videoName.text = video.videoName
            binding.videoDescription.text = video.videoDescription

            binding.root.setOnClickListener { onClick(video) }
        }
    }

    fun getVideoAtPosition(position: Int): Video {
        return mVideos[position]
    }
}
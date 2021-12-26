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

import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.squareup.picasso.Picasso
import net.schueller.peertube.R
import net.schueller.peertube.R.color
import net.schueller.peertube.R.string
import net.schueller.peertube.activity.AccountActivity
import net.schueller.peertube.activity.VideoListActivity
import net.schueller.peertube.activity.VideoListActivity.Companion
import net.schueller.peertube.activity.VideoPlayActivity
import net.schueller.peertube.databinding.ItemCategoryTitleBinding
import net.schueller.peertube.databinding.ItemChannelTitleBinding
import net.schueller.peertube.databinding.RowVideoListBinding
import net.schueller.peertube.helper.APIUrlHelper
import net.schueller.peertube.helper.MetaDataHelper.getDuration
import net.schueller.peertube.helper.MetaDataHelper.getMetaString
import net.schueller.peertube.helper.MetaDataHelper.getOwnerString
import net.schueller.peertube.model.Avatar
import net.schueller.peertube.model.Category
import net.schueller.peertube.model.Channel
import net.schueller.peertube.model.Video
import com.mikepenz.iconics.Iconics.Builder
import net.schueller.peertube.R.id
import net.schueller.peertube.R.menu
import net.schueller.peertube.databinding.ItemTagTitleBinding
import net.schueller.peertube.intents.Intents
import net.schueller.peertube.model.TagVideo

sealed class MultiViewRecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class CategoryViewHolder(private val binding: ItemCategoryTitleBinding): MultiViewRecyclerViewHolder(binding) {
        fun bind(category: Category) {
            binding.textViewTitle.text = category.label
        }
    }

    class ChannelViewHolder(private val binding: ItemChannelTitleBinding): MultiViewRecyclerViewHolder(binding) {
        fun bind(channel: Channel) {

            val context = binding.avatar.context
            val baseUrl = APIUrlHelper.getUrl(context)

            // Avatar
            val avatar: Avatar? = channel.avatar
            if (avatar != null) {
                val avatarPath = avatar.path
                Picasso.get()
                    .load(baseUrl + avatarPath)
                    .placeholder(R.drawable.test_image)
                    .into(binding.avatar)
            }

            binding.textViewTitle.text = channel.displayName
        }
    }

    class TagViewHolder(private val binding: ItemTagTitleBinding): MultiViewRecyclerViewHolder(binding) {
        fun bind(tag: TagVideo) {
            binding.textViewTitle.text = tag.tag
        }
    }

    class VideoViewHolder(private val binding: RowVideoListBinding): MultiViewRecyclerViewHolder(binding) {

        fun bind(video: Video) {

            val context = binding.thumb.context
            val baseUrl = APIUrlHelper.getUrl(context)

            // Temp Loading Image
            Picasso.get()
                .load(baseUrl + video.previewPath)
                .placeholder(R.drawable.test_image)
                .error(R.drawable.test_image)
                .into(binding.thumb)

            // Avatar
            val avatar: Avatar? = video.account.avatar
            if (avatar != null) {
                val avatarPath = avatar.path
                Picasso.get()
                    .load(baseUrl + avatarPath)
                    .into(binding.avatar)
            }
            // set Name
            binding.slRowName.text = video.name

            // set duration (if not live stream)
            if (video.live) {
                binding.videoDuration.setText(string.video_list_live_marker)
                binding.videoDuration.setBackgroundColor(ContextCompat.getColor(context, color.durationLiveBackgroundColor))
            } else {
                binding.videoDuration.text = getDuration(video.duration.toLong())
                binding.videoDuration.setBackgroundColor(ContextCompat.getColor(context, color.durationBackgroundColor))
            }

            // set age and view count
            binding.videoMeta.text = getMetaString(
                video.createdAt,
                video.views,
                context
            )

            // set owner
            val displayNameAndHost = getOwnerString(
                video.account.name,
                video.account.host,
                context
            )
            binding.videoOwner.text = displayNameAndHost

            // video owner click
            binding.videoOwner.setOnClickListener {
                val intent = Intent(context, AccountActivity::class.java)
                intent.putExtra(VideoListActivity.EXTRA_ACCOUNTDISPLAYNAME, displayNameAndHost)
                context.startActivity(intent)
            }

            // avatar click
            binding.avatar.setOnClickListener {
                val intent = Intent(context, AccountActivity::class.java)
                intent.putExtra(Companion.EXTRA_ACCOUNTDISPLAYNAME, displayNameAndHost)
                context.startActivity(intent)
            }

            // Video Click
            binding.root.setOnClickListener {
                val intent = Intent(context, VideoPlayActivity::class.java)
                intent.putExtra(Companion.EXTRA_VIDEOID, video.uuid)
                context.startActivity(intent)
            }

            // More Button
            binding.moreButton.setText(string.video_more_icon)
            Builder().on(binding.moreButton).build()

            binding.moreButton.setOnClickListener { v: View? ->
                val popup = PopupMenu(
                    context,
                    v!!
                )
                popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        id.menu_share -> {
                            Intents.Share(context, video)
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
                popup.inflate(menu.menu_video_row_mode)
                popup.show()
            }
        }
    }


}

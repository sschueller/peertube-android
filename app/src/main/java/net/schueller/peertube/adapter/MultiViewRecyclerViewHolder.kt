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

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.gson.JsonObject
import com.mikepenz.iconics.Iconics.Builder
import com.squareup.picasso.Picasso
import net.schueller.peertube.R
import net.schueller.peertube.R.*
import net.schueller.peertube.activity.AccountActivity
import net.schueller.peertube.activity.VideoListActivity
import net.schueller.peertube.activity.VideoPlayActivity
import net.schueller.peertube.databinding.*
import net.schueller.peertube.fragment.VideoMetaDataFragment
import net.schueller.peertube.helper.APIUrlHelper
import net.schueller.peertube.helper.MetaDataHelper.getCreatorAvatar
import net.schueller.peertube.helper.MetaDataHelper.getCreatorString
import net.schueller.peertube.helper.MetaDataHelper.getDuration
import net.schueller.peertube.helper.MetaDataHelper.getMetaString
import net.schueller.peertube.helper.MetaDataHelper.getOwnerString
import net.schueller.peertube.helper.MetaDataHelper.isChannel
import net.schueller.peertube.intents.Intents
import net.schueller.peertube.model.*
import net.schueller.peertube.model.ui.VideoMetaViewItem
import net.schueller.peertube.network.GetUserService
import net.schueller.peertube.network.GetVideoDataService
import net.schueller.peertube.network.RetrofitInstance
import net.schueller.peertube.network.Session
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


sealed class MultiViewRecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    var videoRating: Rating? = null
    var isLeaveAppExpected = false

    class CategoryViewHolder(private val binding: ItemCategoryTitleBinding) : MultiViewRecyclerViewHolder(binding) {
        fun bind(category: Category) {
            binding.textViewTitle.text = category.label
        }
    }

    class VideoCommentsViewHolder(private val binding: ItemVideoCommentsOverviewBinding) : MultiViewRecyclerViewHolder(binding) {
        fun bind(commentThread: CommentThread) {

            binding.videoCommentsTotalCount.text = commentThread.total.toString()

            if (commentThread.comments.isNotEmpty()) {
                val highlightedComment: Comment = commentThread.comments[0]

                // owner / creator Avatar
                val avatar = highlightedComment.account.avatar
                if (avatar != null) {
                    val baseUrl = APIUrlHelper.getUrl(binding.videoHighlightedAvatar.context)
                    val avatarPath = avatar.path
                    Picasso.get()
                            .load(baseUrl + avatarPath)
                            .into(binding.videoHighlightedAvatar)
                }
                binding.videoHighlightedComment.text = highlightedComment.text
            }
        }
    }


    class VideoMetaViewHolder(private val binding: ItemVideoMetaBinding, private val videoMetaDataFragment: VideoMetaDataFragment?) : MultiViewRecyclerViewHolder(binding) {
        fun bind(videoMetaViewItem: VideoMetaViewItem) {

            val video = videoMetaViewItem.video

            if (video != null && videoMetaDataFragment != null) {

                val context = binding.avatar.context
                val apiBaseURL = APIUrlHelper.getUrlWithVersion(context)
                val videoDataService = RetrofitInstance.getRetrofitInstance(
                        apiBaseURL,
                        APIUrlHelper.useInsecureConnection(context)
                ).create(
                        GetVideoDataService::class.java
                )
                val userService = RetrofitInstance.getRetrofitInstance(
                        apiBaseURL,
                        APIUrlHelper.useInsecureConnection(context)
                ).create(
                        GetUserService::class.java
                )

                // Title
                binding.videoName.text = video.name
                binding.videoOpenDescription.setOnClickListener {
                    videoMetaDataFragment.showDescriptionFragment(video)
                }

                // Thumbs up
                binding.videoThumbsUpWrapper.setOnClickListener {
                    rateVideo(true, video, context, binding)
                }

                // Thumbs Down
                binding.videoThumbsDownWrapper.setOnClickListener {
                    rateVideo(false, video, context, binding)
                }

                // Add to playlist
                binding.videoAddToPlaylistWrapper.setOnClickListener {
                    videoMetaDataFragment.saveToPlaylist(video)
                    Toast.makeText(context, "Saved to playlist", Toast.LENGTH_SHORT).show()
                }

                binding.videoBlockWrapper.setOnClickListener {
                    Toast.makeText(
                            context,
                            context.getString(string.video_feature_not_yet_implemented),
                            Toast.LENGTH_SHORT
                    ).show()
                }

                binding.videoFlagWrapper.setOnClickListener {
                    Toast.makeText(
                            context,
                            context.getString(string.video_feature_not_yet_implemented),
                            Toast.LENGTH_SHORT
                    ).show()
                }

                // video rating
                videoRating = Rating()
                videoRating!!.rating = RATING_NONE // default
                updateVideoRating(video, binding)

                // Retrieve which rating the user gave to this video
                if (Session.getInstance().isLoggedIn) {
                    val call = videoDataService.getVideoRating(video.id)
                    call.enqueue(object : Callback<Rating?> {
                        override fun onResponse(call: Call<Rating?>, response: Response<Rating?>) {
                            videoRating = response.body()
                            updateVideoRating(video, binding)
                        }

                        override fun onFailure(call: Call<Rating?>, t: Throwable) {
                            // Do nothing.
                        }
                    })
                }

                // Share
                binding.videoShare.setOnClickListener {
                    isLeaveAppExpected = true
                    Intents.Share(context, video)
                }

                // hide download if not supported by video
                if (video.downloadEnabled) {
                    binding.videoDownloadWrapper.setOnClickListener {
                        Intents.Download(context, video)
                    }
                } else {
                    binding.videoDownloadWrapper.visibility = GONE
                }

                // created at / views
                binding.videoMeta.text = getMetaString(
                        video.createdAt,
                        video.views,
                        context,
                        true
                )

                // owner / creator
                val displayNameAndHost = getOwnerString(video.account, context)
                if (isChannel(video)) {
                    binding.videoBy.text = context.resources.getString(string.video_by_line, displayNameAndHost)
                } else {
                    binding.videoBy.visibility = GONE
                }

                binding.videoOwner.text = getCreatorString(video, context)

                // owner / creator Avatar
                val avatar = getCreatorAvatar(video, context)
                if (avatar != null) {
                    val baseUrl = APIUrlHelper.getUrl(context)
                    val avatarPath = avatar.path
                    Picasso.get()
                            .load(baseUrl + avatarPath)
                            .into(binding.avatar)
                }

                // videoOwnerSubscribers
                binding.videoOwnerSubscribers.text = context.resources.getQuantityString(R.plurals.video_channel_subscribers, video.channel.followersCount, video.channel.followersCount)


                // video owner click
                binding.videoCreatorInfo.setOnClickListener {
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


                // get subscription status
                var isSubscribed = false

                if (Session.getInstance().isLoggedIn) {
                    val subChannel = video.channel.name + "@" + video.channel.host
                    val call = userService.subscriptionsExist(subChannel)
                    call.enqueue(object : Callback<JsonObject> {
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            if (response.isSuccessful) {
                                // {"video.channel.name + "@" + video.channel.host":true}
                                if (response.body()?.get(video.channel.name + "@" + video.channel.host)!!.asBoolean) {
                                    binding.videoOwnerSubscribeButton.setText(string.unsubscribe)
                                    isSubscribed = true
                                } else {
                                    binding.videoOwnerSubscribeButton.setText(string.subscribe)
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            // Do nothing.
                        }
                    })

                }

                // TODO: update subscriber count
                binding.videoOwnerSubscribeButton.setOnClickListener {
                    if (Session.getInstance().isLoggedIn) {
                        if (!isSubscribed) {
                            val payload = video.channel.name + "@" + video.channel.host
                            val body = "{\"uri\":\"$payload\"}".toRequestBody("application/json".toMediaType())
                            val call = userService.subscribe(body)
                            call.enqueue(object : Callback<ResponseBody?> {
                                override fun onResponse(
                                        call: Call<ResponseBody?>,
                                        response: Response<ResponseBody?>
                                ) {
                                    if (response.isSuccessful) {
                                        binding.videoOwnerSubscribeButton.setText(string.unsubscribe)
                                        isSubscribed = true
                                    }
                                }

                                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                                    // Do nothing.
                                }
                            })
                        } else {
                            AlertDialog.Builder(context)
                                    .setTitle(context.getString(string.video_sub_del_alert_title))
                                    .setMessage(context.getString(string.video_sub_del_alert_msg))
                                    .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                                        // Yes
                                        val payload = video.channel.name + "@" + video.channel.host
                                        val call = userService.unsubscribe(payload)
                                        call.enqueue(object : Callback<ResponseBody?> {
                                            override fun onResponse(
                                                    call: Call<ResponseBody?>,
                                                    response: Response<ResponseBody?>
                                            ) {
                                                if (response.isSuccessful) {
                                                    binding.videoOwnerSubscribeButton.setText(string.subscribe)
                                                    isSubscribed = false
                                                }
                                            }

                                            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                                                // Do nothing.
                                            }
                                        })
                                    }
                                    .setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int ->
                                        // No
                                    }
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show()
                        }
                    } else {
                        Toast.makeText(
                                context,
                                context.getString(string.video_login_required_for_service),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        }
    }

    class ChannelViewHolder(private val binding: ItemChannelTitleBinding) : MultiViewRecyclerViewHolder(binding) {
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

    class TagViewHolder(private val binding: ItemTagTitleBinding) : MultiViewRecyclerViewHolder(binding) {
        fun bind(tag: TagVideo) {
            binding.textViewTitle.text = tag.tag
        }
    }

    class VideoViewHolder(private val binding: RowVideoListBinding) : MultiViewRecyclerViewHolder(binding) {

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
            val avatar = getCreatorAvatar(video, context)
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
            val displayNameAndHost = getOwnerString(video.account, context, true)
            binding.videoOwner.text = getCreatorString(video, context, true)

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


    fun updateVideoRating(video: Video?, binding: ItemVideoMetaBinding) {

        when (videoRating!!.rating) {
            RATING_NONE -> {
                Log.v("MWCVH", "RATING_NONE")
                binding.videoThumbsUp.setImageResource(R.drawable.ic_thumbs_up)
                binding.videoThumbsDown.setImageResource(R.drawable.ic_thumbs_down)
            }
            RATING_LIKE -> {
                Log.v("MWCVH", "RATING_LIKE")
                binding.videoThumbsUp.setImageResource(R.drawable.ic_thumbs_up_filled)
                binding.videoThumbsDown.setImageResource(R.drawable.ic_thumbs_down)
            }
            RATING_DISLIKE -> {
                Log.v("MWCVH", "RATING_DISLIKE")
                binding.videoThumbsUp.setImageResource(R.drawable.ic_thumbs_up)
                binding.videoThumbsDown.setImageResource(R.drawable.ic_thumbs_down_filled)
            }
        }

        // Update the texts
        binding.videoThumbsUpTotal.text = video?.likes.toString()
        binding.videoThumbsDownTotal.text = video?.dislikes.toString()

    }

    /**
     * TODO: move this out and get update when rating changes
     */
    fun rateVideo(like: Boolean, video: Video, context: Context, binding: ItemVideoMetaBinding) {
        if (Session.getInstance().isLoggedIn) {
            val ratePayload: String = when (videoRating!!.rating) {
                RATING_LIKE -> if (like) RATING_NONE else RATING_DISLIKE
                RATING_DISLIKE -> if (like) RATING_LIKE else RATING_NONE
                RATING_NONE -> if (like) RATING_LIKE else RATING_DISLIKE
                else -> if (like) RATING_LIKE else RATING_DISLIKE
            }

            val body = "{\"rating\":\"$ratePayload\"}".toRequestBody("application/json".toMediaType())

            val apiBaseURL = APIUrlHelper.getUrlWithVersion(context)
            val videoDataService = RetrofitInstance.getRetrofitInstance(
                    apiBaseURL, APIUrlHelper.useInsecureConnection(
                    context
            )
            ).create(
                    GetVideoDataService::class.java
            )
            val call = videoDataService.rateVideo(video.id, body)
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                ) {
                    // if 20x, update likes/dislikes
                    if (response.isSuccessful) {
                        val previousRating = videoRating!!.rating

                        // Update the likes/dislikes count of the video, if needed.
                        // This is only a visual trick, as the actual like/dislike count has
                        // already been modified on the PeerTube instance.
                        if (previousRating != ratePayload) {
                            when (previousRating) {
                                RATING_NONE -> if (ratePayload == RATING_LIKE) {
                                    video.likes = video.likes + 1
                                } else {
                                    video.dislikes = video.dislikes + 1
                                }
                                RATING_LIKE -> {
                                    video.likes = video.likes - 1
                                    if (ratePayload == RATING_DISLIKE) {
                                        video.dislikes = video.dislikes + 1
                                    }
                                }
                                RATING_DISLIKE -> {
                                    video.dislikes = video.dislikes - 1
                                    if (ratePayload == RATING_LIKE) {
                                        video.likes = video.likes + 1
                                    }
                                }
                            }
                        }
                        videoRating!!.rating = ratePayload
                        updateVideoRating(video, binding)
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Toast.makeText(
                            context,
                            context.getString(string.video_rating_failed),
                            Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                    context,
                    context.getString(string.video_login_required_for_service),
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val RATING_NONE = "none"
        private const val RATING_LIKE = "like"
        private const val RATING_DISLIKE = "dislike"
        const val EXTRA_VIDEOID = "VIDEOID"
        const val EXTRA_ACCOUNTDISPLAYNAME = "ACCOUNTDISPLAYNAMEANDHOST"

    }

}

package net.schueller.peertube.adapter;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.schueller.peertube.R
import net.schueller.peertube.databinding.*
import net.schueller.peertube.fragment.VideoMetaDataFragment
import net.schueller.peertube.model.*
import net.schueller.peertube.model.ui.OverviewRecycleViewItem
import net.schueller.peertube.model.ui.VideoMetaViewItem
import java.util.ArrayList

class MultiViewRecycleViewAdapter(private val videoMetaDataFragment: VideoMetaDataFragment? = null) : RecyclerView.Adapter<MultiViewRecyclerViewHolder>() {

    private var items = ArrayList<OverviewRecycleViewItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setVideoListData(videoList: VideoList) {
        items.addAll(videoList.videos)
        notifyDataSetChanged()
    }

    fun setVideoData(videos: ArrayList<Video>) {
        items.addAll(videos)
        notifyDataSetChanged()
    }

    fun setVideoMeta(videoMetaViewItem: VideoMetaViewItem) {
        items.add(videoMetaViewItem)
        notifyDataSetChanged()
    }

    fun setCategoryTitle(category: Category) {
        items.add(category)
        notifyDataSetChanged()
    }

    fun setChannelTitle(channel: Channel) {
        items.add(channel)
        notifyDataSetChanged()
    }

    fun setTagTitle(tag: TagVideo) {
        items.add(tag)
        notifyDataSetChanged()
    }

    fun setVideoComment(commentThread: CommentThread) {
        items.add(commentThread)
        notifyDataSetChanged()
    }

    fun clearData() {
        items.clear()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewRecyclerViewHolder {
        return when(viewType){
            R.layout.item_category_title -> MultiViewRecyclerViewHolder.CategoryViewHolder(
                ItemCategoryTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_channel_title -> MultiViewRecyclerViewHolder.ChannelViewHolder(
                ItemChannelTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_tag_title -> MultiViewRecyclerViewHolder.TagViewHolder(
                ItemTagTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.row_video_list -> MultiViewRecyclerViewHolder.VideoViewHolder(
                RowVideoListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_video_meta -> MultiViewRecyclerViewHolder.VideoMetaViewHolder(
                ItemVideoMetaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                videoMetaDataFragment
            )
            R.layout.item_video_comments_overview -> MultiViewRecyclerViewHolder.VideoCommentsViewHolder(
                ItemVideoCommentsOverviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }

    override fun onBindViewHolder(holder: MultiViewRecyclerViewHolder, position: Int) {
        when(holder){
            is MultiViewRecyclerViewHolder.VideoViewHolder -> holder.bind(items[position] as Video)
            is MultiViewRecyclerViewHolder.CategoryViewHolder -> holder.bind(items[position] as Category)
            is MultiViewRecyclerViewHolder.ChannelViewHolder -> holder.bind(items[position] as Channel)
            is MultiViewRecyclerViewHolder.TagViewHolder -> holder.bind(items[position] as TagVideo)
            is MultiViewRecyclerViewHolder.VideoMetaViewHolder -> holder.bind(items[position] as VideoMetaViewItem)
            is MultiViewRecyclerViewHolder.VideoCommentsViewHolder -> holder.bind(items[position] as CommentThread)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){
            is Video -> R.layout.row_video_list
            is Channel -> R.layout.item_channel_title
            is Category -> R.layout.item_category_title
            is TagVideo -> R.layout.item_tag_title
            is VideoMetaViewItem -> R.layout.item_video_meta
            is CommentThread -> R.layout.item_video_comments_overview
            else -> { return 0}
        }
    }
}

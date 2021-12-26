package net.schueller.peertube.adapter;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.schueller.peertube.R
import net.schueller.peertube.databinding.ItemCategoryTitleBinding
import net.schueller.peertube.databinding.ItemChannelTitleBinding
import net.schueller.peertube.databinding.ItemTagTitleBinding
import net.schueller.peertube.databinding.RowVideoListBinding
import net.schueller.peertube.model.Category
import net.schueller.peertube.model.Channel
import net.schueller.peertube.model.TagVideo
import net.schueller.peertube.model.Video
import net.schueller.peertube.model.VideoList
import net.schueller.peertube.model.ui.OverviewRecycleViewItem
import java.util.ArrayList

class MultiViewRecycleViewAdapter : RecyclerView.Adapter<MultiViewRecyclerViewHolder>() {

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
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }

    override fun onBindViewHolder(holder: MultiViewRecyclerViewHolder, position: Int) {
        when(holder){
            is MultiViewRecyclerViewHolder.VideoViewHolder -> holder.bind(items[position] as Video)
            is MultiViewRecyclerViewHolder.CategoryViewHolder -> holder.bind(items[position] as Category)
            is MultiViewRecyclerViewHolder.ChannelViewHolder -> holder.bind(items[position] as Channel)
            is MultiViewRecyclerViewHolder.TagViewHolder -> holder.bind(items[position] as TagVideo)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){
            is Video -> R.layout.row_video_list
            is Channel -> R.layout.item_channel_title
            is Category -> R.layout.item_category_title
            is TagVideo -> R.layout.item_tag_title
            else -> { return 0}
        }
    }
}

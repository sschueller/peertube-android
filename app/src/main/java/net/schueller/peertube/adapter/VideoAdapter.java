package net.schueller.peertube.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.schueller.peertube.R;
import net.schueller.peertube.model.Video;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {


    private ArrayList<Video> videoList;
    private Context context;

    public VideoAdapter(ArrayList<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {

        Picasso.with(this.context)
                .load("https://troll.tv" + videoList.get(position).getPreviewPath())
                .into(holder.thumb);

        holder.name.setText(videoList.get(position).getName());
        holder.description.setText(videoList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView name, description;
        ImageView thumb;

        VideoViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            thumb = itemView.findViewById(R.id.thumb);
            description = itemView.findViewById(R.id.description);
        }
    }

}
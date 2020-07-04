/*
 * Copyright 2018 Stefan Schüller <sschueller@techdroid.com>
 *
 * License: GPL-3.0+
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.Iconics;
import com.squareup.picasso.Picasso;

import net.schueller.peertube.R;
import net.schueller.peertube.activity.AccountActivity;
import net.schueller.peertube.activity.VideoPlayActivity;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.helper.MetaDataHelper;
import net.schueller.peertube.intents.Intents;
import net.schueller.peertube.model.Avatar;
import net.schueller.peertube.model.Video;

import java.util.ArrayList;

import static net.schueller.peertube.activity.VideoListActivity.EXTRA_ACCOUNTDISPLAYNAME;
import static net.schueller.peertube.activity.VideoListActivity.EXTRA_VIDEOID;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {


    private ArrayList<Video> videoList;
    private Context context;
    private String baseUrl;

    public VideoAdapter(ArrayList<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_video, parent, false);

        baseUrl = APIUrlHelper.getUrl(context);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        Picasso.get()
                .load(baseUrl + videoList.get(position).getPreviewPath())
                .into(holder.thumb);


        Avatar avatar = videoList.get(position).getAccount().getAvatar();
        if (avatar != null) {
            String avatarPath = avatar.getPath();
            Picasso.get()
                    .load(baseUrl + avatarPath)
                    .into(holder.avatar);
        }

        // set Name
        holder.name.setText(videoList.get(position).getName());

        // set duration
        holder.videoDuration.setText(MetaDataHelper.getDuration(videoList.get(position).getDuration().longValue()));

        // set age and view count
        holder.videoMeta.setText(
                MetaDataHelper.getMetaString(videoList.get(position).getCreatedAt(),
                        videoList.get(position).getViews(),
                        context
                )
        );

        // set owner
        String displayNameAndHost = MetaDataHelper.getOwnerString(videoList.get(position).getAccount().getName(),
                videoList.get(position).getAccount().getHost(),
                context
        );

        holder.videoOwner.setText(displayNameAndHost);
        // video owner click
        holder.videoOwner.setOnClickListener(v -> {
            Intent intent = new Intent(context, AccountActivity.class);
            intent.putExtra(EXTRA_ACCOUNTDISPLAYNAME, displayNameAndHost);
            context.startActivity(intent);
        });

        // avatar click
        holder.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(context, AccountActivity.class);
            intent.putExtra(EXTRA_ACCOUNTDISPLAYNAME, displayNameAndHost);
            context.startActivity(intent);
        });

        holder.mView.setOnClickListener(v -> {

            // Log.v("VideoAdapter", "click: " + videoList.get(position).getName());

            Intent intent = new Intent(context, VideoPlayActivity.class);
            intent.putExtra(EXTRA_VIDEOID, videoList.get(position).getUuid());
            context.startActivity(intent);

        });

        holder.moreButton.setText(R.string.video_more_icon);
        new Iconics.IconicsBuilder().ctx(context).on(holder.moreButton).build();

        holder.moreButton.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(context, v);
            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.menu_share:
                        Intents.Share(context, videoList.get(position));
                        return true;
                    default:
                        return false;
                }
            });
            popup.inflate(R.menu.menu_video_row_mode);
            popup.show();

        });

    }

    public void setData(ArrayList<Video> data) {
        videoList.addAll(data);
        this.notifyDataSetChanged();
    }

    public void clearData() {
        videoList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView name, videoMeta, videoOwner, moreButton, videoDuration;
        ImageView thumb, avatar;
        View mView;

        VideoViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sl_row_name);
            thumb = itemView.findViewById(R.id.thumb);
            avatar = itemView.findViewById(R.id.avatar);
            videoMeta = itemView.findViewById(R.id.videoMeta);
            videoOwner = itemView.findViewById(R.id.videoOwner);
            moreButton = itemView.findViewById(R.id.moreButton);
            videoDuration = itemView.findViewById(R.id.video_duration);
            mView = itemView;
        }
    }

}
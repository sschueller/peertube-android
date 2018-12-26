package net.schueller.peertube.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.schueller.peertube.R;
import net.schueller.peertube.activity.VideoPlayActivity;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.helper.MetaDataHelper;
import net.schueller.peertube.intents.Intents;
import net.schueller.peertube.model.Avatar;
import net.schueller.peertube.model.Video;

import java.util.ArrayList;

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

        Picasso.with(this.context)
                .load(baseUrl + videoList.get(position).getPreviewPath())
                .into(holder.thumb);


        Avatar avatar = videoList.get(position).getAccount().getAvatar();
        if (avatar != null) {
            String avatarPath = avatar.getPath();
            Picasso.with(this.context)
                    .load(baseUrl + avatarPath)
                    .into(holder.avatar);
        }


        holder.name.setText(videoList.get(position).getName());

        // set age and view count
        holder.videoMeta.setText(
                MetaDataHelper.getMetaString(videoList.get(position).getCreatedAt(),
                        videoList.get(position).getViews(),
                        context
                )
        );

        // set owner
        holder.videoOwner.setText(
                MetaDataHelper.getOwnerString(videoList.get(position).getAccount().getName(),
                        videoList.get(position).getAccount().getHost(),
                        context
                )
        );

        holder.mView.setOnClickListener(v -> {

            // Log.v("VideoAdapter", "click: " + videoList.get(position).getName());

            Intent intent = new Intent(context,VideoPlayActivity.class);
            intent.putExtra(EXTRA_VIDEOID, videoList.get(position).getUuid());
            context.startActivity(intent);

        });

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

        TextView name, videoMeta, videoOwner;
        ImageView thumb, avatar;
        ImageButton moreButton;
        View mView;

        VideoViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            thumb = itemView.findViewById(R.id.thumb);
            avatar = itemView.findViewById(R.id.avatar);
            videoMeta = itemView.findViewById(R.id.videoMeta);
            videoOwner = itemView.findViewById(R.id.videoOwner);
            moreButton = itemView.findViewById(R.id.moreButton);
            mView = itemView;
        }
    }

}
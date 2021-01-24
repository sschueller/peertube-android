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
package net.schueller.peertube.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import net.schueller.peertube.R;
import net.schueller.peertube.activity.SearchServerActivity;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.Server;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import static android.app.Activity.RESULT_OK;


public class ServerSearchAdapter extends RecyclerView.Adapter<ServerSearchAdapter.AccountViewHolder> {


    private ArrayList<Server> serverList;
    private SearchServerActivity activity;
    private String baseUrl;

    public ServerSearchAdapter(ArrayList<Server> serverList, SearchServerActivity activity) {
        this.serverList = serverList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_search_server, parent, false);

        baseUrl = APIUrlHelper.getUrl(activity);

        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {

        holder.name.setText(serverList.get(position).getName());
        holder.host.setText(serverList.get(position).getHost());
        holder.signupAllowed.setText(activity.getString(R.string.server_selection_signup_allowed, activity.getString(
                serverList.get(position).getSignupAllowed() ?
                        R.string.server_selection_signup_allowed_yes :
                        R.string.server_selection_signup_allowed_no
        )));

        holder.videoTotals.setText(
                activity.getString(R.string.server_selection_video_totals,
                        serverList.get(position).getTotalVideos().toString(),
                        serverList.get(position).getTotalLocalVideos().toString()
                ));

        // don't show description if it hasn't been changes from the default
        if (!activity.getString(R.string.peertube_instance_search_default_description).equals(serverList.get(position).getShortDescription())) {
            holder.shortDescription.setText(serverList.get(position).getShortDescription());
            holder.shortDescription.setVisibility(View.VISIBLE);
        } else {
            holder.shortDescription.setVisibility(View.GONE);
        }

        DefaultArtifactVersion serverVersion = new DefaultArtifactVersion(serverList.get(position).getVersion());

        // at least version 2.2
        DefaultArtifactVersion minVersion22 = new DefaultArtifactVersion("2.2.0");
        if (serverVersion.compareTo(minVersion22) >= 0) {
            // show NSFW Icon
            if (serverList.get(position).getNSFW()) {
                holder.isNSFW.setVisibility(View.VISIBLE);
            }
        }


        // select server
        holder.itemView.setOnClickListener(v -> {

            String serverUrl = APIUrlHelper.cleanServerUrl(serverList.get(position).getHost());

            Toast.makeText(activity, activity.getString(R.string.server_selection_set_server, serverUrl), Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            intent.putExtra("serverUrl", serverUrl);
            intent.putExtra("serverName", serverList.get(position).getName());
            activity.setResult(RESULT_OK, intent);

            activity.finish();

        });

//
//
//        holder.moreButton.setText(R.string.video_more_icon);
//        new Iconics.Builder().on(holder.moreButton).build();
//
//        holder.moreButton.setOnClickListener(v -> {
//
//            PopupMenu popup = new PopupMenu(context, v);
//            popup.setOnMenuItemClickListener(menuItem -> {
//                switch (menuItem.getItemId()) {
//                    case R.id.menu_share:
//                        Intents.Share(context, serverList.get(position));
//                        return true;
//                    default:
//                        return false;
//                }
//            });
//            popup.inflate(R.menu.menu_video_row_mode);
//            popup.show();
//
//        });

    }

    public void setData(ArrayList<Server> data) {
        serverList.addAll(data);
        this.notifyDataSetChanged();
    }

    public void clearData() {
        serverList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return serverList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {

        TextView name, host, signupAllowed, shortDescription, videoTotals;
        ImageView isNSFW;

        AccountViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sl_row_name);
            host = itemView.findViewById(R.id.sl_row_host);
            signupAllowed = itemView.findViewById(R.id.sl_row_signup_allowed);
            shortDescription = itemView.findViewById(R.id.sl_row_short_description);
            isNSFW = itemView.findViewById(R.id.sl_row_is_nsfw);
            videoTotals = itemView.findViewById(R.id.sl_row_video_totals);
        }
    }


}
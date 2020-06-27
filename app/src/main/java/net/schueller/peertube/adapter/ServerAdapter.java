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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import net.schueller.peertube.R;
import net.schueller.peertube.activity.SelectServerActivity;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.Server;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;


public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.AccountViewHolder> {


    private ArrayList<Server> serverList;
    private SelectServerActivity activity;
    private String baseUrl;

    public ServerAdapter(ArrayList<Server> serverList, SelectServerActivity activity) {
        this.serverList = serverList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_server, parent, false);

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
        holder.shortDescription.setText(serverList.get(position).getShortDescription());

        holder.itemView.setOnClickListener(v -> {

//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
//            SharedPreferences.Editor editor = sharedPref.edit();

            String serverUrl = APIUrlHelper.cleanServerUrl(serverList.get(position).getHost());

//            editor.putString("pref_api_base", serverUrl);
//            editor.apply();
//
//

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
//        new Iconics.IconicsBuilder().ctx(context).on(holder.moreButton).build();
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

    class AccountViewHolder extends RecyclerView.ViewHolder {

        TextView name, host, signupAllowed, shortDescription;

        AccountViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            host = itemView.findViewById(R.id.host);
            signupAllowed = itemView.findViewById(R.id.signupAllowed);
            shortDescription = itemView.findViewById(R.id.shortDescription);

        }
    }


}
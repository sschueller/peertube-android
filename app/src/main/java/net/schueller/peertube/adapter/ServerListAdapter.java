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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.schueller.peertube.R;

import net.schueller.peertube.activity.SelectServerActivity;
import net.schueller.peertube.activity.ServerAddressBookActivity;
import net.schueller.peertube.database.Server;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.service.LoginService;


import java.util.List;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerViewHolder> {


    private final LayoutInflater mInflater;
    private List<Server> mServers; // Cached copy of Servers

    public ServerListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.row_serverbook, parent, false);
        return new ServerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {

        if (mServers != null) {
            Server current = mServers.get(position);
            holder.serverLabel.setText(current.getServerName());
        } else {
            // Covers the case of data not being ready yet.
            holder.serverLabel.setText("No Servers");
        }

        holder.itemView.setOnClickListener(v -> {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mInflater.getContext());
            SharedPreferences.Editor editor = sharedPref.edit();

            String serverUrl = APIUrlHelper.cleanServerUrl(mServers.get(position).getServerHost());

            editor.putString("pref_api_base", serverUrl);
            editor.apply();

            // attempt authenication
            LoginService.Authenticate(
                    mServers.get(position).getUsername(),
                    mServers.get(position).getPassword()
            );

            ((Activity) mInflater.getContext()).finish();

            Toast.makeText(mInflater.getContext(), mInflater.getContext().getString(R.string.server_selection_set_server, serverUrl), Toast.LENGTH_LONG).show();

            Log.d("ServerListAdapter", "setOnClickListener " + mServers.get(position).getServerHost());
        });


//
//        holder.itemView.setOnLongClickListener(v -> {
//            Log.v("ServerListAdapter", "setOnLongClickListener " + position);
//            return true;
//        });


    }

    public void setServers(List<Server> Servers) {
        mServers = Servers;
        this.notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mServers has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mServers != null)
            return mServers.size();
        else return 0;
    }

    static class ServerViewHolder extends RecyclerView.ViewHolder {
        TextView serverLabel, serverUrl, serverUsername;

        private ServerViewHolder(View itemView) {
            super(itemView);
            serverLabel = itemView.findViewById(R.id.serverLabelRow);
            serverUrl = itemView.findViewById(R.id.serverUrlRow);
        }
    }

}
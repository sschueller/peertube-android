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
package net.schueller.peertube.activity;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.schueller.peertube.R;
import net.schueller.peertube.adapter.ServerListAdapter;
import net.schueller.peertube.database.Server;
import net.schueller.peertube.database.ServerViewModel;
import net.schueller.peertube.fragment.AddServerFragment;


import java.util.Objects;

public class ServerAddressBookActivity extends CommonActivity implements AddServerFragment.OnFragmentInteractionListener {

    private String TAG = "ServerAddressBookActivity";
    public static final String EXTRA_REPLY = "net.schueller.peertube.room.REPLY";

    private ServerViewModel mServerViewModel;
    private AddServerFragment addServerFragment;
    private FloatingActionButton floatingActionButton;
    private FragmentManager fragmentManager;

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_address_book);

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.tool_bar_server_address_book);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        mServerViewModel = new ViewModelProvider(this).get(ServerViewModel.class);

        showServers();

        floatingActionButton = findViewById(R.id.add_server);
        floatingActionButton.setOnClickListener(view -> {

            Log.d(TAG, "Click");

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            addServerFragment = new AddServerFragment();
            fragmentTransaction.replace(R.id.server_book, addServerFragment);
            fragmentTransaction.commit();

            floatingActionButton.hide();

        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void showServers()
    {
        RecyclerView recyclerView = findViewById(R.id.server_list_recyclerview);
        final ServerListAdapter adapter = new ServerListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Delete items on swipe
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int direction) {


                        new AlertDialog.Builder(ServerAddressBookActivity.this)
                                .setTitle(getString(R.string.server_book_del_alert_title))
                                .setMessage(getString(R.string.server_book_del_alert_msg))
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    int position = viewHolder.getAdapterPosition();
                                    Server server = adapter.getServerAtPosition(position);
//                                    Toast.makeText(ServerAddressBookActivity.this, "Deleting " +
//                                            server.getServerName(), Toast.LENGTH_LONG).show();
                                    // Delete the server
                                    mServerViewModel.delete(server);
                                })
                                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                });
        helper.attachToRecyclerView(recyclerView);


        // Update the cached copy of the words in the adapter.
        mServerViewModel.getAllServers().observe(this, adapter::setServers);

    }

    public void addServer(View view)
    {
        Log.d(TAG, "addServer");

        EditText serverLabel = view.findViewById(R.id.serverLabel);
        EditText serverUrl = view.findViewById(R.id.serverUrl);
        EditText serverUsername = view.findViewById(R.id.serverUsername);
        EditText serverPassword = view.findViewById(R.id.serverPassword);

        Server server = new Server(serverLabel.getText().toString());

        server.setServerHost(serverUrl.getText().toString());
        server.setUsername(serverUsername.getText().toString());
        server.setPassword(serverPassword.getText().toString());

        mServerViewModel.insert(server);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(addServerFragment);
        fragmentTransaction.commit();

        floatingActionButton.show();

    }

    public void testServer()
    {

    }

}

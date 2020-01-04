package net.schueller.peertube.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import net.schueller.peertube.R;
import net.schueller.peertube.adapter.ServerListAdapter;
import net.schueller.peertube.fragment.AddServerFragment;
import net.schueller.peertube.fragment.VideoMenuSpeedFragment;


public class ServerAddressBookActivity extends AppCompatActivity implements AddServerFragment.OnFragmentInteractionListener {

    private String TAG = "ServerAddressBookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_address_book);
        Toolbar toolbar = findViewById(R.id.tool_bar_serveraddressbook);
        setSupportActionBar(toolbar);

//        RecyclerView recyclerView = findViewById(R.id.recyclerview);
//        final ServerListAdapter adapter = new ServerListAdapter(this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.add_server);
        fab.setOnClickListener(view -> {

            Log.d(TAG, "Click");

            FragmentManager fragmentManager = getSupportFragmentManager();
            AddServerFragment addServerFragment = (AddServerFragment) fragmentManager.findFragmentByTag(AddServerFragment.TAG);

            if (addServerFragment != null) {

                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .show(addServerFragment)
                        .commit();

                Log.d(TAG, "addServerFragment");
            }

        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

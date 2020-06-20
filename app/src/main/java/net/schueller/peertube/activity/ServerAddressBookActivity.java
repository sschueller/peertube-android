package net.schueller.peertube.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.schueller.peertube.R;
import net.schueller.peertube.adapter.ServerListAdapter;
import net.schueller.peertube.database.Server;
import net.schueller.peertube.database.ServerViewModel;
import net.schueller.peertube.fragment.AddServerFragment;

import java.util.List;


public class ServerAddressBookActivity extends AppCompatActivity implements AddServerFragment.OnFragmentInteractionListener {

    private String TAG = "ServerAddressBookActivity";
    public static final String EXTRA_REPLY = "net.schueller.peertube.room.REPLY";

    private ServerViewModel mServerViewModel;
    private AddServerFragment addServerFragment;
    private FloatingActionButton floatingActionButton;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_address_book);


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



        mServerViewModel.getAllServers().observe(this, new Observer<List<Server>>() {
            @Override
            public void onChanged(@Nullable final List<Server> servers) {
                // Update the cached copy of the words in the adapter.
                adapter.setServers(servers);
            }
        });


    }

    public void addServer(View view)
    {
        Log.d(TAG, "addServer");

        TextView serverLabel = view.findViewById(R.id.serverLabel);
        TextView serverUrl = view.findViewById(R.id.serverUrl);
        TextView serverUsername = view.findViewById(R.id.serverUsername);
        TextView serverPassword = view.findViewById(R.id.serverPassword);

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

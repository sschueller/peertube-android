package net.schueller.peertube.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ServerViewModel extends AndroidViewModel {

    private ServerRepository mRepository;

    private LiveData<List<Server>> mAllServers;

    public ServerViewModel (Application application) {
        super(application);
        mRepository = new ServerRepository(application);
        mAllServers = mRepository.getAllServers();
    }

    LiveData<List<Server>> getAllServers() { return mAllServers; }

    public void insert(Server Server) { mRepository.insert(Server); }
    
}

package net.schueller.peertube.database;

import android.app.Application;
import android.os.AsyncTask;


import androidx.lifecycle.LiveData;

import java.util.List;

class ServerRepository {

    private ServerDao mServerDao;
    private LiveData<List<Server>> mAllServers;

    ServerRepository(Application application) {
        ServerRoomDatabase db = ServerRoomDatabase.getDatabase(application);
        mServerDao = db.serverDao();
        mAllServers = mServerDao.getAll();

    }

    LiveData<List<Server>> getAllServers() {
        return mAllServers;
    }

    void insert (Server Server) {
        new insertAsyncTask(mServerDao).execute(Server);
    }

    private static class insertAsyncTask extends AsyncTask<Server, Void, Void> {

        private ServerDao mAsyncTaskDao;

        insertAsyncTask(ServerDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Server... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}

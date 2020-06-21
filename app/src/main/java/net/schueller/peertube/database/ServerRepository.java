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
        mAllServers = mServerDao.getAllServers();

    }

    LiveData<List<Server>> getAllServers() {
        return mAllServers;
    }

    void insert (Server server) {
        new insertAsyncTask(mServerDao).execute(server);
    }

    public void delete(Server server)  {
        new deleteServerAsyncTask(mServerDao).execute(server);
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

    private static class deleteServerAsyncTask extends AsyncTask<Server, Void, Void> {
        private ServerDao mAsyncTaskDao;

        deleteServerAsyncTask(ServerDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Server... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}

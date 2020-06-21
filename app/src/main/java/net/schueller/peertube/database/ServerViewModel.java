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

    public LiveData<List<Server>> getAllServers() { return mAllServers; }

    public void insert(Server server) { mRepository.insert(server); }

    public void delete(Server server) {mRepository.delete(server);}
    
}

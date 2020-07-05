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

import net.schueller.peertube.model.Video;

import java.util.List;

public class VideoViewModel extends AndroidViewModel {

    private VideoRepository mRepository;

    private LiveData<List<Video>> mAllVideos;

    public VideoViewModel(Application application) {
        super(application);
        mRepository = new VideoRepository(application);
        mAllVideos = mRepository.getAllVideos();
    }

    public LiveData<List<Video>> getAllVideos() { return mAllVideos; }

    public void insert(Video video) { mRepository.insert(video); }

    public void delete(Video video) {mRepository.delete(video);}

}

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

import net.schueller.peertube.model.Video;

import java.util.List;

class VideoRepository {

    private VideoDao mVideoDao;
    private LiveData<List<Video>> mAllVideos;

    VideoRepository(Application application) {
        VideoRoomDatabase db = VideoRoomDatabase.getDatabase(application);
        mVideoDao = db.videoDao();
        mAllVideos = mVideoDao.getAllVideos();

    }

    LiveData<List<Video>> getAllVideos() {
        return mAllVideos;
    }

    void insert (Video video) {
        new insertAsyncTask(mVideoDao).execute(video);
    }

    public void delete(Video video)  {
        new deleteVideoAsyncTask(mVideoDao).execute(video);
    }

    private static class insertAsyncTask extends AsyncTask<Video, Void, Void> {

        private VideoDao mAsyncTaskDao;

        insertAsyncTask(VideoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Video... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteVideoAsyncTask extends AsyncTask<Video, Void, Void> {
        private VideoDao mAsyncTaskDao;

        deleteVideoAsyncTask(VideoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Video... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}

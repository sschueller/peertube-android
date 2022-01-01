/*
 * Copyright (C) 2020 Stefan Schüller <sschueller@techdroid.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import net.schueller.peertube.R
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.schueller.peertube.helper.APIUrlHelper
import net.schueller.peertube.helper.ErrorHelper
import net.schueller.peertube.model.Description
import net.schueller.peertube.model.Video
import net.schueller.peertube.network.GetVideoDataService
import net.schueller.peertube.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoDescriptionFragment : Fragment () {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_video_description, container,
            false
        )

        val video = video

        if (video != null) {

            val apiBaseURL = APIUrlHelper.getUrlWithVersion(context)
            val videoDataService = RetrofitInstance.getRetrofitInstance(
                apiBaseURL,
                APIUrlHelper.useInsecureConnection(context)
            ).create(
                GetVideoDataService::class.java
            )

            // description, get extended if available
            val videoDescription = view.findViewById<TextView>(R.id.description)
            val shortDescription = video.description
            if (shortDescription != null && shortDescription.length > 237) {
                val call = videoDataService.getVideoFullDescription(video.uuid);
                call.enqueue(object : Callback<Description?> {
                    override fun onResponse(call: Call<Description?>, response: Response<Description?>) {
                        val videoFullDescription: Description? = response.body();

                        videoDescription.text = videoFullDescription?.description
                    }
                    override fun onFailure(call: Call<Description?>, t: Throwable) {
                        Log.wtf(TAG, t.fillInStackTrace())
                        ErrorHelper.showToastFromCommunicationError(activity, t)
                    }
                })
            }
            videoDescription.text = shortDescription;

            val closeButton = view.findViewById<ImageButton>(R.id.video_description_close_button)
            closeButton.setOnClickListener {
                videoMetaDataFragment!!.hideDescriptionFragment()
            }

            // video privacy
            val videoPrivacy = view.findViewById<TextView>(R.id.video_privacy);
            videoPrivacy.text = video!!.privacy.label;

            // video category
            val videoCategory = view.findViewById<TextView>(R.id.video_category);
            videoCategory.text = video!!.category.label;

            // video privacy
            val videoLicense = view.findViewById<TextView>(R.id.video_license);
            videoLicense.text = video!!.licence.label;

            // video language
            val videoLanguage = view.findViewById<TextView>(R.id.video_language);
            videoLanguage.text = video!!.language.label;

            // video privacy
            val videoTags = view.findViewById<TextView>(R.id.video_tags);
            videoTags.text = android.text.TextUtils.join(", ", video!!.tags);
        }


        return view
    }

    companion object {
        private var video: Video? = null
        private var videoMetaDataFragment: VideoMetaDataFragment? = null
        const val TAG = "VideoDescr"
        fun newInstance(mVideo: Video?, mVideoMetaDataFragment: VideoMetaDataFragment): VideoDescriptionFragment {
            video = mVideo
            videoMetaDataFragment = mVideoMetaDataFragment
            return VideoDescriptionFragment()
        }
    }
}
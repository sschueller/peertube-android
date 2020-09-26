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
package net.schueller.peertube.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.Iconics;
import com.squareup.picasso.Picasso;

import net.schueller.peertube.R;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.helper.ErrorHelper;
import net.schueller.peertube.helper.MetaDataHelper;
import net.schueller.peertube.intents.Intents;
import net.schueller.peertube.model.Account;
import net.schueller.peertube.model.Avatar;
import net.schueller.peertube.model.Rating;
import net.schueller.peertube.model.Video;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.network.Session;
import net.schueller.peertube.service.VideoPlayerService;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoMetaDataFragment extends Fragment {

    private static final String TAG = "VideoMetaDataFragment";

    private static final String RATING_NONE = "none";
    private static final String RATING_LIKE = "like";
    private static final String RATING_DISLIKE = "dislike";

    private Rating videoRating;
    private ColorStateList defaultTextColor;

    private boolean leaveAppExpected = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_meta, container, false);
    }

    @Override
    public void onPause()
    {
        leaveAppExpected = false;
        super.onPause();
    }

    public boolean isLeaveAppExpected()
    {
        return leaveAppExpected;
    }

    public void updateVideoMeta(Video video, VideoPlayerService mService) {
        Context context = getContext();
        Activity activity = getActivity();

        String apiBaseURL = APIUrlHelper.getUrlWithVersion(context);
        GetVideoDataService videoDataService = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetVideoDataService.class);

        // Thumbs up
        Button thumbsUpButton = activity.findViewById(R.id.video_thumbs_up);
        defaultTextColor = thumbsUpButton.getTextColors();
        thumbsUpButton.setText(R.string.video_thumbs_up_icon);
        new Iconics.IconicsBuilder().ctx(context).on(thumbsUpButton).build();
        thumbsUpButton.setOnClickListener(v -> {
            rateVideo(true, video);
        });

        // Thumbs Down
        Button thumbsDownButton = activity.findViewById(R.id.video_thumbs_down);
        thumbsDownButton.setText(R.string.video_thumbs_down_icon);
        new Iconics.IconicsBuilder().ctx(context).on(thumbsDownButton).build();
        thumbsDownButton.setOnClickListener(v -> {
            rateVideo(false, video);
        });

        // video rating
        videoRating = new Rating();
        videoRating.setRating(RATING_NONE); // default
        updateVideoRating(video);

        // Retrieve which rating the user gave to this video
        if (Session.getInstance().isLoggedIn()) {
            Call<Rating> call = videoDataService.getVideoRating(video.getId());
            call.enqueue(new Callback<Rating>() {

                @Override
                public void onResponse(Call<Rating> call, Response<Rating> response) {
                    videoRating = response.body();
                    updateVideoRating(video);
                }

                @Override
                public void onFailure(Call<Rating> call, Throwable t) {
                    ErrorHelper.showToastFromCommunicationError( getActivity(), t );
                    // Do nothing.
                }
            });
        }

        // Share
        Button videoShareButton = activity.findViewById(R.id.video_share);
        videoShareButton.setText(R.string.video_share_icon);
        new Iconics.IconicsBuilder().ctx(context).on(videoShareButton).build();
        videoShareButton.setOnClickListener(v ->
                                            {
                                                leaveAppExpected = true;
                                                Intents.Share( context, video );
                                            } );

        // Download
        Button videoDownloadButton = activity.findViewById(R.id.video_download);
        videoDownloadButton.setText(R.string.video_download_icon);
        new Iconics.IconicsBuilder().ctx(context).on(videoDownloadButton).build();
        videoDownloadButton.setOnClickListener(v -> {
            // get permission to store file
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                leaveAppExpected = true;
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intents.Download(context, video);
                } else {
                    Toast.makeText(context, getString(R.string.video_download_permission_error), Toast.LENGTH_LONG).show();
                }
            } else {
                Intents.Download(context, video);
            }
        });

        Account account = video.getAccount();

        // owner / creator Avatar
        Avatar avatar = account.getAvatar();
        if (avatar != null) {
            ImageView avatarView = activity.findViewById(R.id.avatar);
            String baseUrl = APIUrlHelper.getUrl(context);
            String avatarPath = avatar.getPath();
            Picasso.get()
                    .load(baseUrl + avatarPath)
                    .into(avatarView);
        }


        // title / name
        TextView videoName = activity.findViewById(R.id.sl_row_name);
        videoName.setText(video.getName());

        // created at / views
        TextView videoMeta = activity.findViewById(R.id.videoMeta);
        videoMeta.setText(
                MetaDataHelper.getMetaString(
                        video.getCreatedAt(),
                        video.getViews(),
                        context
                )
        );

        // owner / creator
        TextView videoOwner = activity.findViewById(R.id.videoOwner);
        videoOwner.setText(
                MetaDataHelper.getOwnerString(video.getAccount().getName(),
                        video.getAccount().getHost(),
                        context
                )
        );

        // description
        TextView videoDescription = activity.findViewById(R.id.description);
        videoDescription.setText(video.getDescription());

        // video privacy
        TextView videoPrivacy = activity.findViewById(R.id.video_privacy);
        videoPrivacy.setText(video.getPrivacy().getLabel());

        // video category
        TextView videoCategory = activity.findViewById(R.id.video_category);
        videoCategory.setText(video.getCategory().getLabel());

        // video privacy
        TextView videoLicense = activity.findViewById(R.id.video_license);
        videoLicense.setText(video.getLicence().getLabel());

        // video language
        TextView videoLanguage = activity.findViewById(R.id.video_language);
        videoLanguage.setText(video.getLanguage().getLabel());

        // video privacy
        TextView videoTags = activity.findViewById(R.id.video_tags);
        videoTags.setText(android.text.TextUtils.join(", ", video.getTags()));

        // more button
        TextView moreButton = activity.findViewById(R.id.moreButton);
        moreButton.setText(R.string.video_more_icon);
        new Iconics.IconicsBuilder().ctx(context).on(moreButton).build();

        moreButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.video_more_report:
                        Log.v(TAG, "Report");
                        Toast.makeText(context, "Not Implemented", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.video_more_blacklist:
                        Log.v(TAG, "Blacklist");
                        Toast.makeText(context, "Not Implemented", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            });
            popup.inflate(R.menu.menu_video_more);
            popup.show();
        });

        // video player options
        TextView videoOptions = activity.findViewById(R.id.exo_more);
        videoOptions.setText(R.string.video_more_icon);
        new Iconics.IconicsBuilder().ctx(context).on(videoOptions).build();

        videoOptions.setOnClickListener(v -> {
            VideoOptionsFragment videoOptionsFragment =
                    VideoOptionsFragment.newInstance(mService, video.getFiles());
            videoOptionsFragment.show(getActivity().getSupportFragmentManager(),
                    VideoOptionsFragment.TAG);
        });

    }

    void updateVideoRating(Video video) {
        Button thumbsUpButton = getActivity().findViewById(R.id.video_thumbs_up);
        Button thumbsDownButton = getActivity().findViewById(R.id.video_thumbs_down);

        TypedValue typedValue = new TypedValue();

        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int accentColor = a.getColor(0, 0);

        // Change the color of the thumbs
        switch (videoRating.getRating()) {
            case RATING_NONE:
                thumbsUpButton.setTextColor(defaultTextColor);
                thumbsDownButton.setTextColor(defaultTextColor);
                break;
            case RATING_LIKE:
                thumbsUpButton.setTextColor(accentColor);
                thumbsDownButton.setTextColor(defaultTextColor);
                break;
            case RATING_DISLIKE:
                thumbsUpButton.setTextColor(defaultTextColor);
                thumbsDownButton.setTextColor(accentColor);
                break;
        }

        // Update the texts
        TextView thumbsDownTotal = getActivity().findViewById(R.id.video_thumbs_down_total);
        TextView thumbsUpTotal = getActivity().findViewById(R.id.video_thumbs_up_total);
        thumbsUpTotal.setText(String.valueOf(video.getLikes()));
        thumbsDownTotal.setText(String.valueOf(video.getDislikes()));

        a.recycle();
    }

    void rateVideo(Boolean like, Video video) {
        if (Session.getInstance().isLoggedIn()) {
            final String ratePayload;

            switch (videoRating.getRating()) {
                case RATING_LIKE:
                    ratePayload = like ? RATING_NONE : RATING_DISLIKE;
                    break;
                case RATING_DISLIKE:
                    ratePayload = like ? RATING_LIKE : RATING_NONE;
                    break;
                case RATING_NONE:
                default:
                    ratePayload = like ? RATING_LIKE : RATING_DISLIKE;
                    break;
            }

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json"), "{\"rating\":\"" + ratePayload + "\"}");

            String apiBaseURL = APIUrlHelper.getUrlWithVersion(getContext());
            GetVideoDataService videoDataService = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetVideoDataService.class);

            Call<ResponseBody> call = videoDataService.rateVideo(video.getId(), body);

            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //Log.v(TAG, response.toString());

                    // if 20x, update likes/dislikes
                    if (response.isSuccessful()) {
                        String previousRating = videoRating.getRating();

                        // Update the likes/dislikes count of the video, if needed.
                        // This is only a visual trick, as the actual like/dislike count has
                        // already been modified on the PeerTube instance.
                        if (!previousRating.equals(ratePayload)) {
                            switch (previousRating) {
                                case RATING_NONE:
                                    if (ratePayload.equals(RATING_LIKE)) {
                                        video.setLikes(video.getLikes() + 1);
                                    } else {
                                        video.setDislikes(video.getDislikes() + 1);
                                    }
                                    break;
                                case RATING_LIKE:
                                    video.setLikes(video.getLikes() - 1);
                                    if (ratePayload.equals(RATING_DISLIKE)) {
                                        video.setDislikes(video.getDislikes() + 1);
                                    }
                                    break;
                                case RATING_DISLIKE:
                                    video.setDislikes(video.getDislikes() - 1);
                                    if (ratePayload.equals(RATING_LIKE)) {
                                        video.setLikes(video.getLikes() + 1);
                                    }
                                    break;
                            }
                        }

                        videoRating.setRating(ratePayload);
                        updateVideoRating(video);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), getString(R.string.video_rating_failed), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), getString(R.string.video_login_required_for_service), Toast.LENGTH_SHORT).show();
        }
    }

}

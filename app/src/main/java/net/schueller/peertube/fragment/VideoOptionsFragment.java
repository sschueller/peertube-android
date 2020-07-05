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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mikepenz.iconics.Iconics;

import net.schueller.peertube.R;
import net.schueller.peertube.model.File;
import net.schueller.peertube.service.VideoPlayerService;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class VideoOptionsFragment extends BottomSheetDialogFragment {

    private static VideoPlayerService videoPlayerService;
    private static ArrayList<File> files;

    public static final String TAG = "VideoOptions";


    public static VideoOptionsFragment newInstance(VideoPlayerService mService, ArrayList<File> mFiles) {
        videoPlayerService = mService;
        files = mFiles;
        return new VideoOptionsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video_options_popup_menu, container,
                false);

        LinearLayout menuHolder = view.findViewById(R.id.video_options_popup);

        // Video Speed
        LinearLayout menuRow = (LinearLayout) inflater.inflate(R.layout.row_popup_menu, container);
        TextView iconView = menuRow.findViewById(R.id.video_quality_icon);
        TextView textView = menuRow.findViewById(R.id.video_quality_text);

        textView.setText(
                getString(
                        R.string.menu_video_options_playback_speed,
                        getCurrentVideoPlaybackSpeedString(videoPlayerService.getPlayBackSpeed()
                        )
                )
        );


        iconView.setText(R.string.video_option_speed_icon);
        new Iconics.IconicsBuilder().ctx(getContext()).on(iconView).build();
        textView.setOnClickListener(view1 -> {
            VideoMenuSpeedFragment videoMenuSpeedFragment =
                    VideoMenuSpeedFragment.newInstance(videoPlayerService);
            videoMenuSpeedFragment.show(requireActivity().getSupportFragmentManager(),
                    VideoMenuSpeedFragment.TAG);
        });
        menuHolder.addView(menuRow);

        // Video Quality
        LinearLayout menuRow2 = (LinearLayout) inflater.inflate(R.layout.row_popup_menu, container);
        TextView iconView2 = menuRow2.findViewById(R.id.video_quality_icon);
        TextView textView2 = menuRow2.findViewById(R.id.video_quality_text);
        textView2.setText(String.format(getString(R.string.menu_video_options_quality), getCurrentVideoQuality(files)));
        iconView2.setText(R.string.video_option_quality_icon);
        new Iconics.IconicsBuilder().ctx(getContext()).on(iconView2).build();
        textView2.setOnClickListener(view1 -> {
            VideoMenuQualityFragment videoMenuQualityFragment =
                    VideoMenuQualityFragment.newInstance(getContext(), files);
            videoMenuQualityFragment.show(requireActivity().getSupportFragmentManager(),
                    VideoMenuQualityFragment.TAG);
        });
        menuHolder.addView(menuRow2);

        return view;

    }

    private String getCurrentVideoQuality(ArrayList<File> files) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        Integer videoQuality = sharedPref.getInt(getString(R.string.pref_quality_key), 0);

        for (File file : files) {
            if (videoQuality.equals(file.getResolution().getId())) {
                return file.getResolution().getLabel();
            }
        }
        // Returning Automated as a placeholder
        return getString(R.string.menu_video_options_quality_automated);
    }

    private String getCurrentVideoPlaybackSpeedString(float playbackSpeed) {
        String speed = String.valueOf(playbackSpeed);
        // Remove all non-digit characters from the string
        speed = speed.replaceAll("[^0-9]", "");

        // Dynamically get the localized string corresponding to the speed
        @StringRes int stringId = getResources().getIdentifier("video_speed_" + speed, "string", videoPlayerService.getPackageName());
        return getString(stringId);
    }
}
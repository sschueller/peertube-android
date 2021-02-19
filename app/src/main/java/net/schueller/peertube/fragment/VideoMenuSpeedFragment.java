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
package net.schueller.peertube.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mikepenz.iconics.Iconics;

import net.schueller.peertube.R;
import net.schueller.peertube.service.VideoPlayerService;

import androidx.annotation.Nullable;

public class VideoMenuSpeedFragment extends BottomSheetDialogFragment {

    private static VideoPlayerService videoPlayerService;
    public static final String TAG = "VideoMenuSpeed";

    private TextView speed05Icon;
    private TextView speed075Icon;
    private TextView speed10Icon;
    private TextView speed125Icon;
    private TextView speed15Icon;
    private TextView speed20Icon;

    public static VideoMenuSpeedFragment newInstance(VideoPlayerService mService) {
        videoPlayerService = mService;
        return new VideoMenuSpeedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video_options_speed_popup_menu, container,
                false);

        // Icons
        speed05Icon = view.findViewById(R.id.video_speed05_icon);
        speed075Icon = view.findViewById(R.id.video_speed075_icon);
        speed10Icon = view.findViewById(R.id.video_speed10_icon);
        speed125Icon = view.findViewById(R.id.video_speed125_icon);
        speed15Icon = view.findViewById(R.id.video_speed15_icon);
        speed20Icon = view.findViewById(R.id.video_speed20_icon);

        // Buttons
        TextView speed05 = view.findViewById(R.id.video_speed05);
        TextView speed075 = view.findViewById(R.id.video_speed075);
        TextView speed10 = view.findViewById(R.id.video_speed10);
        TextView speed125 = view.findViewById(R.id.video_speed125);
        TextView speed15 = view.findViewById(R.id.video_speed15);
        TextView speed20 = view.findViewById(R.id.video_speed20);

        setDefaultVideoSpeed();

        // Attach the listener
        speed05.setOnClickListener(v -> setVideoSpeed(0.5f, speed05Icon));
        speed075.setOnClickListener(v -> setVideoSpeed(0.75f, speed075Icon));
        speed10.setOnClickListener(v -> setVideoSpeed(1.0f, speed10Icon));
        speed125.setOnClickListener(v -> setVideoSpeed(1.25f, speed125Icon));
        speed15.setOnClickListener(v -> setVideoSpeed(1.5f, speed15Icon));
        speed20.setOnClickListener(v -> setVideoSpeed(2.0f, speed20Icon));

        return view;

    }


    private void setVideoSpeed(Float speed, TextView icon) {

        speed05Icon.setText("");
        speed075Icon.setText("");
        speed10Icon.setText("");
        speed125Icon.setText("");
        speed15Icon.setText("");
        speed20Icon.setText("");

        videoPlayerService.setPlayBackSpeed(speed);

        icon.setText(R.string.video_speed_active_icon);
        new Iconics.Builder().on(icon).build();
    }

    private void setDefaultVideoSpeed() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String speed = sharedPref.getString(getString(R.string.pref_video_speed_key), "1.0");

        switch (speed) {
            case "0.5":
                setVideoSpeed(0.5f, speed05Icon);
                break;
            case "0.75":
                setVideoSpeed(0.75f, speed075Icon);
                break;
            case "1.0":
                setVideoSpeed(1.0f, speed10Icon);
                break;
            case "1.25":
                setVideoSpeed(1.25f, speed125Icon);
                break;
            case "1.5":
                setVideoSpeed(1.5f, speed15Icon);
                break;
            case "2.0":
                setVideoSpeed(2.0f, speed20Icon);
                break;
        }
    }

}
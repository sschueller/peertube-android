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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mikepenz.iconics.Iconics;
import net.schueller.peertube.R;
import net.schueller.peertube.service.VideoPlayerService;

import androidx.annotation.Nullable;

public class VideoOptionsFragment extends BottomSheetDialogFragment {

    private static VideoPlayerService videoPlayerService;

    private TextView speed05Icon;
    private TextView speed10Icon;
    private TextView speed15Icon;
    private TextView speed20Icon;

    public static VideoOptionsFragment newInstance(VideoPlayerService mService) {
        videoPlayerService = mService;
        return new VideoOptionsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video_options_popup_menu, container,
                false);

        // Icons
        speed05Icon = view.findViewById(R.id.video_speed05_icon);
        speed10Icon = view.findViewById(R.id.video_speed10_icon);
        speed15Icon = view.findViewById(R.id.video_speed15_icon);
        speed20Icon = view.findViewById(R.id.video_speed20_icon);

        // Buttons
        TextView speed05 = view.findViewById(R.id.video_speed05);
        TextView speed10 = view.findViewById(R.id.video_speed10);
        TextView speed15 = view.findViewById(R.id.video_speed15);
        TextView speed20 = view.findViewById(R.id.video_speed20);

        // Default
        setVideoSpeed(1.0f, speed10Icon);

        // Attach the listener
        speed05.setOnClickListener(v -> setVideoSpeed(0.5f, speed05Icon));
        speed10.setOnClickListener(v -> setVideoSpeed(1.0f, speed10Icon));
        speed15.setOnClickListener(v -> setVideoSpeed(1.5f, speed15Icon));
        speed20.setOnClickListener(v -> setVideoSpeed(2.0f, speed20Icon));

        return view;

    }


    private void setVideoSpeed(Float speed, TextView icon) {

        speed05Icon.setText("");
        speed10Icon.setText("");
        speed15Icon.setText("");
        speed20Icon.setText("");

        videoPlayerService.setPlayBackSpeed(speed);

        icon.setText(R.string.video_speed_active_icon);
        new Iconics.IconicsBuilder().ctx(getContext()).on(icon).build();
    }

}
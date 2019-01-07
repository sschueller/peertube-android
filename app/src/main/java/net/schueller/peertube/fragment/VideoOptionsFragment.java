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
import android.util.Log;
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
        LinearLayout menuRow = (LinearLayout) inflater.inflate(R.layout.row_popup_menu, null);
        TextView iconView = menuRow.findViewById(R.id.video_quality_icon);
        TextView textView = menuRow.findViewById(R.id.video_quality_text);
        textView.setText("Video Speed");
        iconView.setText(R.string.video_speed_active_icon);
        new Iconics.IconicsBuilder().ctx(getContext()).on(iconView).build();
        textView.setOnClickListener(view1 -> {
            VideoMenuSpeedFragment videoMenuSpeedFragment =
                    VideoMenuSpeedFragment.newInstance(videoPlayerService);
            videoMenuSpeedFragment.show(getActivity().getSupportFragmentManager(),
                    VideoMenuSpeedFragment.TAG);
        });
        menuHolder.addView(menuRow);

        // Video Quality
        LinearLayout menuRow2 = (LinearLayout) inflater.inflate(R.layout.row_popup_menu, null);
        TextView iconView2 = menuRow2.findViewById(R.id.video_quality_icon);
        TextView textView2 = menuRow2.findViewById(R.id.video_quality_text);
        textView2.setText("Video Quality");
        iconView2.setText(R.string.video_speed_active_icon);
        new Iconics.IconicsBuilder().ctx(getContext()).on(iconView2).build();
        textView2.setOnClickListener(view1 -> {
            VideoMenuQualityFragment videoMenuQualityFragment =
                    VideoMenuQualityFragment.newInstance(files);
            videoMenuQualityFragment.show(getActivity().getSupportFragmentManager(),
                    videoMenuQualityFragment.TAG);
        });
        menuHolder.addView(menuRow2);

        return view;

    }

}
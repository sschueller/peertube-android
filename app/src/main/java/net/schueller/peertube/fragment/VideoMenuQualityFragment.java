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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import net.schueller.peertube.R;
import net.schueller.peertube.model.File;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class VideoMenuQualityFragment extends BottomSheetDialogFragment {

    private static ArrayList<File> mFiles;
    public static final String TAG = "VideoMenuQuality";

    public static VideoMenuQualityFragment newInstance(ArrayList<File> files) {
        mFiles = files;
        return new VideoMenuQualityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video_options_quality_popup_menu, container,
                false);

        for (File file :mFiles) {

            LinearLayout menuRow = (LinearLayout) inflater.inflate(R.layout.row_popup_menu, null);

            TextView iconView = menuRow.findViewById(R.id.video_quality_icon);
            TextView textView = menuRow.findViewById(R.id.video_quality_text);

            Log.v(TAG, file.getResolution().getLabel());
            textView.setText(file.getResolution().getLabel());

            textView.setOnClickListener(view1 -> { Log.v(TAG, file.getResolution().getLabel()); });
            iconView.setOnClickListener(view1 -> { Log.v(TAG, file.getResolution().getLabel()); });

            // Add to menu
            LinearLayout menuHolder = view.findViewById(R.id.video_quality_menu);
            menuHolder.addView(menuRow);

        }

        return view;

    }


}
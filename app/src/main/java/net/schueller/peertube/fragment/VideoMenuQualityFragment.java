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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import net.schueller.peertube.model.Resolution;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class VideoMenuQualityFragment extends BottomSheetDialogFragment {

    private static ArrayList<File> mFiles;
    public static final String TAG = "VideoMenuQuality";
    private static File autoQualityFile;

    public static VideoMenuQualityFragment newInstance(Context context, ArrayList<File> files) {

        mFiles = files;

        // Auto quality
        if (autoQualityFile == null) {
            autoQualityFile = new File();
            Resolution autoQualityResolution = new Resolution();
            autoQualityResolution.setId(999999);
            autoQualityResolution.setLabel(context.getString(R.string.menu_video_options_quality_automated));
            autoQualityFile.setId(999999);
            autoQualityFile.setResolution(autoQualityResolution);
        }
        if (!mFiles.contains(autoQualityFile)) {
            mFiles.add(0, autoQualityFile);
        }

        return new VideoMenuQualityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video_options_quality_popup_menu, container,
                false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        Integer videoQuality = sharedPref.getInt(getString(R.string.pref_quality_key), 999999);

        for (File file : mFiles) {

            LinearLayout menuRow = (LinearLayout) inflater.inflate(R.layout.row_popup_menu, container);

            TextView iconView = menuRow.findViewById(R.id.video_quality_icon);
            iconView.setId(file.getResolution().getId());
            TextView textView = menuRow.findViewById(R.id.video_quality_text);

            Log.v(TAG, file.getResolution().getLabel());
            textView.setText(file.getResolution().getLabel());

            textView.setOnClickListener(view1 -> {
//                Log.v(TAG, file.getResolution().getLabel());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.pref_quality_key), file.getResolution().getId());
                editor.apply();

                for (File fileV : mFiles) {
                    TextView iconViewV = view.findViewById(fileV.getResolution().getId());
                    if (iconViewV != null) {
                        iconViewV.setText("");
                    }
                }

                iconView.setText(R.string.video_quality_active_icon);
                new Iconics.Builder().on(iconView).build();

                //TODO: set new video quality on running video

            });

            // Add to menu
            LinearLayout menuHolder = view.findViewById(R.id.video_quality_menu);
            menuHolder.addView(menuRow);

            // Set current
            if (videoQuality.equals(file.getResolution().getId())) {
                iconView.setText(R.string.video_quality_active_icon);
                new Iconics.Builder().on(iconView).build();
            }

        }

        return view;

    }


}
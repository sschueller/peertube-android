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
            autoQualityResolution.setId(0);
            autoQualityResolution.setLabel(context.getString(R.string.menu_video_options_quality_automated));
            autoQualityFile.setId(0);
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
        Integer videoQuality = sharedPref.getInt("pref_quality", 0);

        for (File file : mFiles) {

            LinearLayout menuRow = (LinearLayout) inflater.inflate(R.layout.row_popup_menu, null);

            TextView iconView = menuRow.findViewById(R.id.video_quality_icon);
            iconView.setId(file.getResolution().getId());
            TextView textView = menuRow.findViewById(R.id.video_quality_text);

            Log.v(TAG, file.getResolution().getLabel());
            textView.setText(file.getResolution().getLabel());

            textView.setOnClickListener(view1 -> {
//                Log.v(TAG, file.getResolution().getLabel());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("pref_quality", file.getResolution().getId());
                editor.apply();

                for (File fileV : mFiles) {
                    TextView iconViewV = view.findViewById(fileV.getResolution().getId());
                    iconViewV.setText("");
                }

                iconView.setText(R.string.video_quality_active_icon);
                new Iconics.IconicsBuilder().ctx(getContext()).on(iconView).build();

                //TODO: set new video quality on running video

            });

            // Add to menu
            LinearLayout menuHolder = view.findViewById(R.id.video_quality_menu);
            menuHolder.addView(menuRow);

            // Set current
            if (videoQuality.equals(file.getResolution().getId())) {
                iconView.setText(R.string.video_quality_active_icon);
                new Iconics.IconicsBuilder().ctx(getContext()).on(iconView).build();
            }

        }

        return view;

    }


}
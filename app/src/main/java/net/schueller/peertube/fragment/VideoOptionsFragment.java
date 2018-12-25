package net.schueller.peertube.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.schueller.peertube.R;

import androidx.annotation.Nullable;

public class VideoOptionsFragment extends BottomSheetDialogFragment {

    public static VideoOptionsFragment newInstance() {
        return new VideoOptionsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_video_options_fragment, container,
                false);

        // get the views and attach the listener

        return view;

    }
}
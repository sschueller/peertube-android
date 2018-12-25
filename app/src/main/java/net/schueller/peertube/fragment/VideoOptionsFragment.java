package net.schueller.peertube.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.schueller.peertube.R;
import net.schueller.peertube.service.VideoPlayerService;

import androidx.annotation.Nullable;

public class VideoOptionsFragment extends BottomSheetDialogFragment {

    private static VideoPlayerService videoPlayerService;

    public static VideoOptionsFragment newInstance(VideoPlayerService mService) {
        videoPlayerService = mService;
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

        //Playback speed buttons
        TextView speed05 = view.findViewById(R.id.video_speed05);
        TextView speed10 = view.findViewById(R.id.video_speed10);
        TextView speed15 = view.findViewById(R.id.video_speed15);
        TextView speed20 = view.findViewById(R.id.video_speed20);

        //Playback speed controls
        speed05.setOnClickListener(v -> videoPlayerService.setPlayBackSpeed(0.5f));
        speed10.setOnClickListener(v -> videoPlayerService.setPlayBackSpeed(1.0f));
        speed15.setOnClickListener(v -> videoPlayerService.setPlayBackSpeed(1.5f));
        speed20.setOnClickListener(v -> videoPlayerService.setPlayBackSpeed(2.0f));

        return view;

    }
}
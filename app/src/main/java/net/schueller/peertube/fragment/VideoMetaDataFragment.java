package net.schueller.peertube.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import net.schueller.peertube.R;
import net.schueller.peertube.activity.VideoPlayActivity;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.helper.MetaDataHelper;
import net.schueller.peertube.intents.Intents;
import net.schueller.peertube.model.Account;
import net.schueller.peertube.model.Avatar;
import net.schueller.peertube.model.Video;
import net.schueller.peertube.network.GetVideoDataService;
import net.schueller.peertube.network.RetrofitInstance;
import net.schueller.peertube.network.Session;
import net.schueller.peertube.service.VideoPlayerService;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoMetaDataFragment extends Fragment {

    private static final String TAG = "VideoMetaDataFragment";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_meta, container, false);
    }

    public void updateVideoMeta(Video video, VideoPlayerService mService) {

        Context context = getContext();
        Activity activity = getActivity();


        // Thumbs up
        Button thumbsUpButton = activity.findViewById(R.id.video_thumbs_up);
        thumbsUpButton.setText(R.string.video_thumbs_up_icon);
        new Iconics.IconicsBuilder().ctx(context).on(thumbsUpButton).build();
        thumbsUpButton.setOnClickListener(v -> {

            if (Session.getInstance().isLoggedIn()) {

                // TODO: move this out helper/service
                RequestBody body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json"),
                        "{\"rating\":\"like\"}"
                );

                String apiBaseURL = APIUrlHelper.getUrlWithVersion(context);
                GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetVideoDataService.class);

                Call<ResponseBody> call = service.rateVideo(video.getId(), body);

                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        Log.v(TAG, response.toString() );

                        // if 20x update likes
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Rating Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "You must login to use this service", Toast.LENGTH_SHORT).show();

            }

        });

        TextView thumbsUpButtonTotal = activity.findViewById(R.id.video_thumbs_up_total);
        thumbsUpButtonTotal.setText(video.getLikes().toString());

        // Thumbs Down
        TextView thumbsDownButton = activity.findViewById(R.id.video_thumbs_down);
        thumbsDownButton.setText(R.string.video_thumbs_down_icon);
        new Iconics.IconicsBuilder().ctx(context).on(thumbsDownButton).build();
        thumbsDownButton.setOnClickListener(v -> {

            if (Session.getInstance().isLoggedIn()) {

                // TODO: move this out helper/service
                RequestBody body = RequestBody.create(
                        okhttp3.MediaType.parse("application/json"),
                        "{\"rating\":\"dislike\"}"
                );

                String apiBaseURL = APIUrlHelper.getUrlWithVersion(context);
                GetVideoDataService service = RetrofitInstance.getRetrofitInstance(apiBaseURL).create(GetVideoDataService.class);

                Call<ResponseBody> call = service.rateVideo(video.getId(), body);

                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        // if 20x update likes

                        Log.v(TAG, response.toString() );

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Rating Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "You must login to use this service", Toast.LENGTH_SHORT).show();

            }

        });

        TextView thumbsDownButtonTotal = activity.findViewById(R.id.video_thumbs_down_total);
        thumbsDownButtonTotal.setText(video.getDislikes().toString());

        // Share
        TextView videoShareButton = activity.findViewById(R.id.video_share);
        videoShareButton.setText(R.string.video_share_icon);
        new Iconics.IconicsBuilder().ctx(context).on(videoShareButton).build();
        videoShareButton.setOnClickListener(v -> Intents.Share(context, video));

        // Download
        TextView videoDownloadButton = activity.findViewById(R.id.video_download);
        videoDownloadButton.setText(R.string.video_download_icon);
        new Iconics.IconicsBuilder().ctx(context).on(videoDownloadButton).build();
        videoDownloadButton.setOnClickListener(v -> Toast.makeText(context, "Not Implemented", Toast.LENGTH_SHORT).show());

        // add to playlist
        TextView videoSaveButton = activity.findViewById(R.id.video_save);
        videoSaveButton.setText(R.string.video_save_icon);
        new Iconics.IconicsBuilder().ctx(context).on(videoSaveButton).build();
        videoSaveButton.setOnClickListener(v -> Toast.makeText(context, "Not Implemented", Toast.LENGTH_SHORT).show());


        Account account = video.getAccount();

        // owner / creator Avatar
        Avatar avatar = account.getAvatar();
        if (avatar != null) {
            ImageView avatarView = activity.findViewById(R.id.avatar);
            String baseUrl = APIUrlHelper.getUrl(context);
            String avatarPath = avatar.getPath();
            Picasso.with(context)
                    .load(baseUrl + avatarPath)
                    .into(avatarView);
        }


        // title / name
        TextView videoName = activity.findViewById(R.id.name);
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

        // video langauge
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
                        Log.v(TAG, "Report" );
                        Toast.makeText(context, "Not Implemented", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.video_more_blacklist:
                        Log.v(TAG, "Blacklist" );
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
                    VideoOptionsFragment.newInstance(mService);
            videoOptionsFragment.show(getActivity().getSupportFragmentManager(),
                    "video_options_fragment");
        });

    }

}

package net.schueller.peertube.intents;

import android.content.Context;
import android.content.Intent;
import net.schueller.peertube.helper.APIUrlHelper;
import net.schueller.peertube.model.Video;


public class Intents {


    /**
     * https://troll.tv/videos/watch/6edbd9d1-e3c5-4a6c-8491-646e2020469c
     *
     * @param context context
     * @param video video
     */
    public static void Share(Context context, Video video) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, video.getName());
        intent.putExtra(Intent.EXTRA_TEXT, APIUrlHelper.getShareUrl(context, video.getUuid()) );
        intent.setType("text/plain");

        context.startActivity(intent);

    }
}

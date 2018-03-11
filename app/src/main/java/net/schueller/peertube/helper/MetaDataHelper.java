package net.schueller.peertube.helper;

import android.content.Context;
import android.text.format.DateUtils;
import net.schueller.peertube.R;
import java.util.Date;

public class MetaDataHelper {

    public static String getMetaString(Date getCreatedAt, Integer viewCount, Context context) {
        return DateUtils.
                getRelativeTimeSpanString(getCreatedAt.getTime()).toString() +
                context.getResources().getString(R.string.meta_data_seperator) +
                viewCount + context.getResources().getString(R.string.meta_data_views);
    }

    public static String getOwnerString(String accountName, String serverHost, Context context) {
        return accountName +
                context.getResources().getString(R.string.meta_data_owner_seperator) +
                serverHost;
    }

}

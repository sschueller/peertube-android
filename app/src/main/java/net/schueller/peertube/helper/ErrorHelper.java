package net.schueller.peertube.helper;

import android.content.Context;
import android.widget.Toast;

import net.schueller.peertube.R;

import java.io.IOException;

import retrofit2.HttpException;

public class ErrorHelper {

    public static void showToastFromCommunicationError( Context context, Throwable throwable ) {
        if (throwable instanceof IOException ) {
            //handle network error
            Toast.makeText( context, context.getString( R.string.network_error), Toast.LENGTH_SHORT).show();
        } else if (throwable instanceof HttpException ) {
            //handle HTTP error response code
            Toast.makeText(context, context.getString(R.string.api_error), Toast.LENGTH_SHORT).show();
        } else {
            //handle other exceptions
            Toast.makeText(context, context.getString(R.string.api_error), Toast.LENGTH_SHORT).show();
        }
    }
}

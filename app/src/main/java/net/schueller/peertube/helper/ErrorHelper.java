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

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
package net.schueller.peertube.intents

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import net.schueller.peertube.helper.APIUrlHelper
import android.webkit.MimeTypeMap
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.app.ActivityCompat
import net.schueller.peertube.R
import net.schueller.peertube.model.Video
import android.content.ContextWrapper

import android.app.Activity





object Intents {
    private const val TAG = "Intents"

    /**
     * https://troll.tv/videos/watch/6edbd9d1-e3c5-4a6c-8491-646e2020469c
     *
     * @param context context
     * @param video   video
     */
    // TODO, offer which version to download
    @JvmStatic
    fun Share(context: Context, video: Video) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_SUBJECT, video.name)
        intent.putExtra(Intent.EXTRA_TEXT, APIUrlHelper.getShareUrl(context, video.uuid))
        intent.type = "text/plain"
        context.startActivity(intent)
    }

    /**
     * @param context context
     * @param video   video
     */
    // TODO, offer which version to download
    fun Download(context: Context, video: Video) {

        // deal withe permissions here

        // get permission to store file
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val activity = getActivity(context)

            if (activity != null) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
            }

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startDownload(video, context)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.video_download_permission_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            startDownload(video, context)
        }

    }

    private fun startDownload(video: Video, context: Context)
    {
        if (video.files.size > 0) {
            val url = video.files[0].fileDownloadUrl
            // make sure it is a valid filename
            val destFilename = video.name.replace(
                "[^a-zA-Z0-9]".toRegex(),
                "_"
            ) + "." + MimeTypeMap.getFileExtensionFromUrl(
                URLUtil.guessFileName(url, null, null)
            )

            //Toast.makeText(context, destFilename, Toast.LENGTH_LONG).show();
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDescription(video.description)
            request.setTitle(video.name)
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destFilename)

            // get download service and enqueue file
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        } else {
            Toast.makeText(context, R.string.api_error, Toast.LENGTH_LONG).show()
        }
    }


    private fun getActivity(context: Context?): Activity? {
        if (context == null) {
            return null
        } else if (context is ContextWrapper) {
            return if (context is Activity) {
                context
            } else {
                getActivity(context.baseContext)
            }
        }
        return null
    }
}
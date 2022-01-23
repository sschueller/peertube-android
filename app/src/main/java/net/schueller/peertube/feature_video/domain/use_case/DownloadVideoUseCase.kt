package net.schueller.peertube.feature_video.domain.use_case

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import net.schueller.peertube.feature_video.domain.model.Video
import javax.inject.Inject

class DownloadVideoUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(video: Video)
    {
        startDownload(video)

    //        // deal withe permissions here
//        if (activity !== null) {
//            Log.v("DWNL", "download video")
//            // get permission to store file
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    activity,
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    0
//                )
//
//                if (ActivityCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//                    startDownload(video, context)
//                } else {
//                    Toast.makeText(
//                        context,
//                        "R.string.video_download_permission_error",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            } else {
//                startDownload(video, context)
//            }
//        } else {
//            Log.v("DWNL", "no activity")
//        }
    }
//
//    fun requestDownloadPermission(activity: Activity): Boolean
//    {
//        // get permission to store file
//        if (ActivityCompat.checkSelfPermission(
//                activity.applicationContext,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                context.findActivity(),
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                0
//            )
//            return if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                true
//            } else {
//                Toast.makeText(
//                    context,
//                    "R.string.video_download_permission_error",
//                    Toast.LENGTH_LONG
//                ).show()
//                false
//            }
//        } else {
//            return true
//        }
//    }


    private fun startDownload(video: Video)
    {
        if (video.files?.isNotEmpty() == true) {
            val url = video.files[0].fileDownloadUrl
            // make sure it is a valid filename
            val destFilename = video.name?.replace(
                "[^a-zA-Z0-9]".toRegex(),
                "_"
            ) + "." + MimeTypeMap.getFileExtensionFromUrl(
                URLUtil.guessFileName(url, null, null)
            )

            //Toast.makeText(context, destFilename, Toast.LENGTH_LONG).show();
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDescription(video.description)
            request.setTitle(video.name)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destFilename)

            // get download service and enqueue file
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        } else {
            Toast.makeText(context, "R.string.api_error", Toast.LENGTH_LONG).show()
        }
    }

}
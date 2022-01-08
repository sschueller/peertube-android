package net.schueller.peertube.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "watch_later")
data class Video(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        @ColumnInfo(name = "video_name")
        var videoName: String,

        @ColumnInfo(name = "video_description")
        var videoDescription: String?

) : Parcelable
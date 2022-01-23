package net.schueller.peertube.feature_video.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentThreadDto(
    val total: Int,
    val totalNotDeletedComments: Int,
    @SerializedName("data")
    val comments: List<CommentDto>
)

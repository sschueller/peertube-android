package net.schueller.peertube.feature_video.data.remote.dto

import java.util.*

data class CommentDto (
    val id: Int,
    val url: String,
    val text: String,
    val threadId: Int,
    val inReplyToCommentId: Int? = null,
    val videoId: Int,
    val createdAt: Date,
    val updatedAt: Date,
    val deletedAt: Date? = null,
    val isDeleted: Boolean,
    val totalRepliesFromVideoAuthor: Int,
    val totalReplies: Int,
    val account: AccountDto
)
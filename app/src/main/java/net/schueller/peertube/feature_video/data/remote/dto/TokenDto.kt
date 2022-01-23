package net.schueller.peertube.feature_video.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TokenDto(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("expires_in")
    val expiresIn: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("token_type")
    val tokenType: String

)

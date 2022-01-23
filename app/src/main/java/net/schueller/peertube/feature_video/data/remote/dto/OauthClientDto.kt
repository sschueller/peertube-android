package net.schueller.peertube.feature_video.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OauthClientDto(
    @SerializedName("client_id")
    val clientId: String,

    @SerializedName("client_secret")
    val clientSecret: String

)

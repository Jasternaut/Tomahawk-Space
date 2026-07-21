package com.tomahawk.space.data.model

import com.google.gson.annotations.SerializedName

data class ApodResponse(
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("date") val date: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("media_type") val mediaType: String
)

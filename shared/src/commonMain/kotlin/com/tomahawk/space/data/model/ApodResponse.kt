package com.tomahawk.space.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApodResponse(
    @SerialName("title") val title: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("hdurl") val hdUrl: String? = null,
    @SerialName("date") val date: String = "",
    @SerialName("explanation") val explanation: String = "",
    @SerialName("media_type") val mediaType: String = "",
    @SerialName("basic_html") val basicHtml: String = ""
)
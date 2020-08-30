package com.github.search.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ErrorMessage(
    @SerializedName("message") val message: String? = "",
    @SerializedName("documentation_url") val documentationUrl: String? = ""
) : Parcelable
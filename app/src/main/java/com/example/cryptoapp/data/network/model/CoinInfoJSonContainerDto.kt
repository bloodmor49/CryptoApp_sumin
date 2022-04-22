package com.example.cryptoapp.data.network.model

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * JSON объект с информацией о валюте.
 */
data class CoinInfoJSonContainerDto (
    @SerializedName("RAW")
    @Expose
    val json: JsonObject? = null
)

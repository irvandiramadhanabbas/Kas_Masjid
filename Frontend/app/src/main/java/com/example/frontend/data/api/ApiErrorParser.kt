package com.example.frontend.data.api

import okhttp3.ResponseBody

private fun ResponseBody?.safeString(): String? =
    try { this?.string() } catch (_: Exception) { null }

fun Throwable.toApiMessage(defaultMsg: String): String {
    return if (this is retrofit2.HttpException) {
        val raw = response()?.errorBody()?.string()
        if (!raw.isNullOrBlank()) {
            try {
                org.json.JSONObject(raw).optString("message").ifBlank { defaultMsg }
            } catch (_: Exception) {
                defaultMsg
            }
        } else defaultMsg
    } else {
        message ?: defaultMsg
    }
}


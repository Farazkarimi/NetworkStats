package com.example.networkstats.Utils

import android.text.TextUtils
import android.util.Log
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


object StringUtils {

    private val TAG = StringUtils::class.java.simpleName

    fun encodeHtml(content: String): String {
        val textEncoding = "UTF-8"
        var encodedContent = content
        try {
            encodedContent = URLEncoder.encode(encodedContent, textEncoding)
        } catch (ex: UnsupportedEncodingException) {
            Log.e("encodeHtml(): Unsupported encoding %s", textEncoding, ex)
        }

        return encodedContent.replace("\\+".toRegex(), " ")
    }

    fun toFloat(value: String): Float {
        if (TextUtils.isEmpty(value))
            return 0.0f
        try {
            return java.lang.Float.parseFloat(value)
        } catch (ex: NumberFormatException) {
            return 0.0f
        }

    }

    fun isNullEmptyOrWhitespace(value: String?): Boolean {
        return value == null || value.isEmpty() || value.trim { it <= ' ' }.isEmpty()
    }

    fun substring(s: String, start: Int, length: Int): String {
        return s.substring(start, Math.min(start + length, s.length))
    }

    fun mayBeJson(string: String): Boolean {
        return !isNullEmptyOrWhitespace(string) && ("null" == string
                || string.startsWith("[") && string.endsWith("]") || string.startsWith("{") && string.endsWith("}"))
    }
}
package com.bll.lnkwrite.net

import android.util.ArrayMap
import android.util.Pair
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

/**
 * 快速生成　RequestBody
 */
object RequestUtils {
    private val gson = Gson()
    private val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    /**
     * 根据map 参数返回 一个 requestbody
     * @param map 封装好数据的map
     */
    fun getBody(map: Map<String, Any>): RequestBody {
        return RequestBody.create(mediaType, gson.toJson(map))
    }


    /**
     * 传入可变参数的 pair
     */
    fun getBody(vararg pairs: Pair<Any, Any>): RequestBody {
        val map = ArrayMap<Any, Any>(pairs.size)
        pairs.forEach { map[it.first] = it.second }
        return RequestBody.create(mediaType, gson.toJson(map))
    }



}
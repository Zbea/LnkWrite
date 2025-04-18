package com.bll.lnkwrite.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.ArrayMap
import com.bll.lnkwrite.mvp.model.User
import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
import java.io.*

/**
 * 数据存储类　
 * 优先从map 中读取，如果map 中没有，再从文件或者　sharedPreferences 中读取。
 * 写入的时候，用handler 写入
 */
object SPUtil {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var map: ArrayMap<String, Any>
    private val gson = Gson()
    private lateinit var rootFile: File
    private val strs= mutableListOf("token","password","account")

    fun getUserId():String{
        val userStr=if (getObj("user", User::class.java) ==null){
            ""
        }
        else{
            getObj("user", User::class.java)?.accountId!!.toString()
        }
        return userStr
    }

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        map = ArrayMap()
        rootFile = context.cacheDir
    }

    fun putString(key: String, value: String) {
        var keyStr=key
        if (!strs.contains(key)){
            keyStr= getUserId() + key
        }
        map[keyStr] = value
        Schedulers.io().run {
            editor.putString(keyStr, value).apply()
        }
    }

    fun getString(key: String): String {
        var keyStr=key
        if (!strs.contains(key)){
            keyStr= getUserId() + key
        }
        var s = map[keyStr]
        if (s == null) {
            s = sharedPreferences.getString(keyStr, "")
            if (s != null) {
                map[keyStr] = s
            }
        }
        return s as String
    }

    fun putInt(key: String, value: Int) {
        map[getUserId() +key] = value
        Schedulers.io().run {
            editor.putInt(getUserId() +key, value).apply()
        }
    }

    fun getInt(key: String): Int {
        var result = map[getUserId() +key]
        if (result == null) {
            result = sharedPreferences.getInt(getUserId() +key, 0)
            map[getUserId() +key] = result
        }
        return result as Int
    }

    fun putBoolean(key: String, value: Boolean) {
        map[getUserId() +key] = value
        Schedulers.io().run {
            editor.putBoolean(getUserId() +key, value).apply()
        }
    }

    fun getBoolean(key: String): Boolean {
        var result = map[getUserId() +key]
        if (result == null) {
            result = sharedPreferences.getBoolean(getUserId() +key, false)
            map[getUserId() +key] = result
        }
        return result as Boolean
    }

    fun putObj(key: String, any: Any) {
        var keyStr=key
        if (key != "user"){
            keyStr= getUserId() + key
        }
        map[keyStr] = any
        Schedulers.io().run {
            val file = File(rootFile, keyStr)
            if (file.exists()) {
                file.delete()
            }
            file.writeText(gson.toJson(any))
        }
    }


    fun <T> getObj(key: String, cls: Class<T>): T? {
        var keyStr=key
        if (key != "user"){
            keyStr= getUserId() + key
        }
        var result = map[keyStr]
        if (result == null) {
            val file = File(rootFile, keyStr)
            if (file.exists()) {
                val text = file.readText()
                result = gson.fromJson(text, cls)
                if (result != null) {
                    map[keyStr] = result
                }
            }
            else{
                return null
            }
        }
        return result as T
    }

    fun removeObj(key: String): Any? {
        var keyStr=key
        if (key != "user"){
            keyStr= getUserId() + key
        }
        val file = File(rootFile, keyStr)
        if (file.exists()) {
            file.delete()
        }
        return map.remove(keyStr)
    }

    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象

     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }

}
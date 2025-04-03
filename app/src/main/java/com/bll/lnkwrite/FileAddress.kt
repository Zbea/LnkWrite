package com.bll.lnkwrite

import com.bll.lnkwrite.Constants.APK_PATH
import com.bll.lnkwrite.Constants.BOOK_PATH
import com.bll.lnkwrite.Constants.DOCUMENT_PATH
import com.bll.lnkwrite.Constants.HOMEWORK_PATH
import com.bll.lnkwrite.Constants.IMAGE_PATH
import com.bll.lnkwrite.Constants.SCREEN_PATH
import com.bll.lnkwrite.Constants.TEXTBOOK_PATH
import com.bll.lnkwrite.Constants.ZIP_PATH
import com.bll.lnkwrite.mvp.model.User
import com.bll.lnkwrite.utils.SPUtil

class FileAddress {

    private fun getUserId():Long{
        val mUser=SPUtil.getObj("user", User::class.java)
        return mUser?.accountId ?: 0
    }

    private fun getUserAccount():String{
        val mUser=SPUtil.getObj("user", User::class.java)
        return mUser?.account!!
    }

    /**
     * 书籍目录地址
     */
    fun getPathTextBookCatalog(path:String):String{
        return "$path/catalog.txt"
    }
    /**
     * 书籍图片地址
     */
    fun getPathTextBookPicture(path:String):String{
        return "$path/contents"
    }
    /**
     * 书籍地址
     * /storage/emulated/0/Books
     */
    fun getPathBook(fileName: String):String{
        return "$BOOK_PATH/${getUserId()}/$fileName"
    }
    /**
     * 书籍手写地址
     * /storage/emulated/0/Notes
     */
    fun getPathBookDraw(fileName: String):String{
        return "$BOOK_PATH/${getUserId()}/${fileName}draw"
    }

    fun getPathHomeworkBook(fileName: String):String{
        return "$TEXTBOOK_PATH/${getUserId()}/homeworkBook/$fileName"
    }
    fun getPathHomeworkBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/${getUserId()}/homeworkBook/${fileName}draw"
    }
    fun getPathTextBook(fileName: String):String{
        return "$TEXTBOOK_PATH/${getUserId()}/textbook/$fileName"
    }
    fun getPathTextBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/${getUserId()}/textbook/${fileName}draw"
    }
    /**
     * zip保存地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/Zip/fileName.zip
     */
    fun getPathZip(fileName:String):String{
        return "$ZIP_PATH/$fileName.zip"
    }
    /**
     * apk下载地址
     */
    fun getPathApk(fileName: String):String{
        return "$APK_PATH/$fileName.apk"
    }

    /**
     * 笔记保存地址
     */
    fun getPathNote(typeStr: String?,noteBookStr: String?):String{
        return "$IMAGE_PATH/${getUserId()}/note/$typeStr/$noteBookStr"
    }

    /**
     * 画本保存地址
     */
    fun getPathPainting(typeStr: String):String{
        return "$IMAGE_PATH/${getUserId()}/painting/$typeStr"
    }

    /**
     * 日程保存地址
     */
    fun getPathDate(dateStr:String):String{
        return "$IMAGE_PATH/${getUserId()}/date/$dateStr"
    }

    /**
     * 获取作业批改路径
     */
    fun getPathCorrect(id:Int):String{
        return "$HOMEWORK_PATH/${getUserId()}/$id"
    }

    /**
     * 随笔文件路径
     */
    fun getPathFreeNote(title:String):String{
        return "$IMAGE_PATH/${getUserId()}/freeNote/$title"
    }

    /**
     * 计划总览路径
     */
    fun getPathPlan(year:Int,month:Int):String{
        return "$IMAGE_PATH/${getUserId()}/month/$year$month"
    }
    /**
     * 计划总览路径
     */
    fun getPathPlan(startTime:String):String{
        return "$IMAGE_PATH/${getUserId()}/week/$startTime"
    }

    /**
     * 日历背景下载地址
     */
    fun getPathCalender(fileName: String):String{
        return "$IMAGE_PATH/${getUserId()}/calender/$fileName"
    }


    /**
     * 日记路径
     */
    fun getPathDiary(time:String):String{
        return "$IMAGE_PATH/${getUserId()}/diary/$time"
    }

    /**
     * 壁纸
     */
    fun getPathImage(typeStr: String,contentId: Int):String{
        return "$IMAGE_PATH/${getUserId()}/$typeStr/$contentId"
    }

    /**
     * 截图
     */
    fun getPathScreen(typeStr: String):String{
        return "$SCREEN_PATH/${getUserId()}/$typeStr"
    }

    /**
     * 文档
     */
    fun getPathDocument(typeStr: String):String{
        return "$DOCUMENT_PATH/${getUserAccount()}/$typeStr"
    }
}
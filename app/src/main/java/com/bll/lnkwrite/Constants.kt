package com.bll.lnkwrite

import android.os.Environment

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//    ┃　　　┃   神兽保佑
//    ┃　　　┃   代码无BUG！
//    ┃　　　┗━━━┓
//    ┃　　　　　　　┣┓
//    ┃　　　　　　　┏┛
//    ┗┓┓┏━┳┓┏┛
//      ┃┫┫　┃┫┫
//      ┗┻┛　┗┻┛
/**
 * desc: 常量  分辨率为 1404x1872，屏幕尺寸为 10.3
 */
object Constants {
        const val WIDTH = 1404
        const val HEIGHT = 1872 //38->52 50->69
        const val halfYear=90*24*60*60*1000
        const val dayLong=24*60*60*1000
        const val weekTime=7*24*60*60*1000
        const val STATUS_BAR_SHOW=2147483647//永不消失
        const val SCREEN_LEFT = 1//左屏
        const val SCREEN_RIGHT = 2//右屏
        const val SCREEN_FULL = 3//全屏
        const val DEBUG="debug"

//                const val URL_BASE = "https://api2.qinglanmb.com/v1/"
        const val URL_BASE = "http://192.168.3.100:10800/v1/"
        const val UPDATE_URL="http://cdn.qinglanmb.com/"

        ///storage/emulated/0/Android/data/yourPackageName/files/Zip
        val ZIP_PATH = MyApplication.mContext.getExternalFilesDir("Zip")?.path
        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH = MyApplication.mContext.getExternalFilesDir("APK")?.path
        val IMAGE_PATH = MyApplication.mContext.getExternalFilesDir("Image")?.path
        val HOMEWORK_PATH = MyApplication.mContext.getExternalFilesDir("Homework")?.path
        val TEXTBOOK_PATH = MyApplication.mContext.getExternalFilesDir("TextBookFile")!!.path
        val BOOK_PATH =Environment.getExternalStoragePublicDirectory("Books").absolutePath
        val SCREEN_PATH =Environment.getExternalStoragePublicDirectory("Screenshots").absolutePath
        val DOCUMENT_PATH =Environment.getExternalStoragePublicDirectory("Documents").absolutePath

        //eventbus通知标志
        const val AUTO_REFRESH_EVENT = "AutoRefreshEvent" //每天刷新
        const val DATE_DRAWING_EVENT = "DateDrawingEvent" //日历手写事件
        const val BOOK_EVENT = "BookEvent"
        const val BOOK_TYPE_EVENT = "BookTypeEvent"
        const val TEXT_BOOK_EVENT = "TextBookEvent"
        const val NOTE_TYPE_REFRESH_EVENT = "NoteTypeRefreshEvent"
        const val NOTE_EVENT = "NoteEvent"
        const val MESSAGE_EVENT = "MessageEvent"
        const val STUDENT_EVENT="StudentEvent"
        const val HOMEWORK_CORRECT_EVENT="CorrectEvent"
        const val APP_INSTALL_EVENT="AppInstallEvent"
        const val APP_INSTALL_INSERT_EVENT="AppInstallInsertEvent"
        const val APP_UNINSTALL_EVENT="AppUnInstallEvent"
        const val CALENDER_EVENT = "CalenderEvent"
        const val CALENDER_SET_EVENT = "CalenderSetEvent"
        const val REFRESH_STUDENT_PERMISSION_EVENT = "RefreshStudentPermission"
        const val SCREENSHOT_MANAGER_EVENT="ScreenshotManagerEvent"//截图管理刷新
        const val PAINTING_TYPE_EVENT="PaintingTypeEvent"//画本管理刷新

        const val PACKAGE_PPT= "com.htfyun.dualdocreader"
        const val PACKAGE_GEOMETRY="com.geometry"
        const val PACKAGE_READER = "com.geniatech.knote.reader"
        const val PACKAGE_SYSTEM_UPDATE = "com.htfyun.firmwareupdate"
        const val PACKAGE_INSTALLER= "com.android.packageinstaller"
//        const val PACKAGE_SYSTEM_UPDATE = "com.fctek.firmwareupdate"
//        const val PACKAGE_PPT= "com.fctek.dualdocreader"
//        const val PACKAGE_UI_BAR="com.fctek.systemui"

//        const val NET_REFRESH = "com.fctek.systemui.refresh"
//        const val SYSTEM_APP_STATUS_SHOW = "com.fctek.firmwareupdate.status.show"//判断当前系统更新是否运行
        const val ACTION_DAY_REFRESH = "com.bll.lnkwrite.refresh"//每天0刷新
        const val NET_REFRESH="com.htfyun.blackwhitebar.refresh"
        const val SYSTEM_APP_STATUS_SHOW = "com.htfyun.firmwareupdate.status.show"//判断当前系统更新是否运行

        const val LOGIN_BROADCAST_EVENT="com.bll.lnkwrite.account.login"
        const val LOGOUT_BROADCAST_EVENT="com.bll.lnkwrite.account.logout"
        const val NETWORK_CONNECTION_COMPLETE_EVENT = "NetworkConnectionCompleteEvent"//网络连接成功

        const val INTENT_SCREEN_LABEL = "android.intent.extra.LAUNCH_SCREEN"//打开页面在那个屏
        const val INTENT_DRAWING_FOCUS = "android.intent.extra.KEEP_FOCUS"//手写设置焦点

        const val SP_PAINTING_DRAW_TYPE = "PaintingDrawTYpe"//画笔类型

        //OTA SN前缀
        const val PERSIST_OTA_SN_PREFIX = "persist.ota.sn.prefix"
        const val SN = "SN"
        const val KEY = "Key"
        const val VERSION_NO = "VersionNO"

        const val SP_DIARY_BG_SET ="dirayBgRes"//日记
        const val SP_UPDATE_SYSTEM_STATUS = "UpdateSystemStatus"
        const val SP_PRIVACY_PASSWORD = "PrivacyPassword"//私密密码
}



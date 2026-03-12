package com.bll.lnkwrite

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.util.Log
import com.bll.lnkwrite.utils.NetworkUtil
import org.greenrobot.eventbus.EventBus

class MyBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action==Constants.SYSTEM_APP_STATUS_SHOW){
            val isShow=intent.getBooleanExtra("isShow",false)
            DataBeanManager.isSystemUpdateShow=isShow
        }
        if (!MethodManager.isLogin()){
            return
        }
        when(intent.action){
            "android.intent.action.PACKAGE_ADDED"->{
                EventBus.getDefault().post(Constants.APP_INSTALL_EVENT)
            }
            "android.intent.action.PACKAGE_REMOVED"->{
                EventBus.getDefault().post(Constants.APP_UNINSTALL_EVENT)
            }
            Constants.ACTION_DAY_REFRESH->{
                Log.d("debug","每天刷新")
                EventBus.getDefault().postSticky(Constants.AUTO_REFRESH_EVENT)
            }
            Constants.NET_REFRESH->{
                if (NetworkUtil.isNetworkConnected()){
                    EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT )
                }
            }
            DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED->{
                val displayManager=context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                val wifiDisplayStatus=displayManager.getWifiDisplayStatus()
                if (wifiDisplayStatus!=null&&wifiDisplayStatus.getActiveDisplay()!=null){
                    DataBeanManager.isConnectDisplayStatus=true
                    Log.d("debug", "监听投屏连接")
                }
                else{
                    DataBeanManager.isConnectDisplayStatus=false
                    Log.d("debug", "监听投屏断开")
                }
            }
        }
    }
}
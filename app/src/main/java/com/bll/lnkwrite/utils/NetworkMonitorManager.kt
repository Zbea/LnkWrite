package com.bll.lnkwrite.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.bll.lnkwrite.Constants
import org.greenrobot.eventbus.EventBus

class NetworkMonitorManager(private val context: Context) {
    // 记录当前网络状态，避免重复发送事件
    private var isNetworkAvailable = false
    private var isWifiConnected = false

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // 网络可用时回调
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            checkAndPostNetworkState()
        }

        // 网络丢失时回调
        override fun onLost(network: Network) {
            super.onLost(network)
            checkAndPostNetworkState()
        }

        // 网络能力变化时回调（如WiFi/蜂窝网络切换）
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            checkAndPostNetworkState()
        }

        // 网络不可用时回调
        override fun onUnavailable() {
            super.onUnavailable()
            checkAndPostNetworkState()
        }
    }

    /**
     * 启动网络监听
     */
    fun startMonitor() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            // 注册默认网络回调，监听所有网络状态变化
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            // 首次启动时主动检查一次网络状态
            checkAndPostNetworkState()
            Log.d(Constants.DEBUG, "网络监听已启动")
        } catch (e: Exception) {
            Log.e(Constants.DEBUG, "注册NetworkCallback失败: ${e.message}", e)
        }
    }

    /**
     * 停止网络监听（必须调用，避免内存泄漏）
     */
    fun stopMonitor() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            // 重置状态
            isNetworkAvailable = false
            isWifiConnected = false
            Log.d(Constants.DEBUG, "网络监听已停止")
        } catch (e: Exception) {
            Log.w(Constants.DEBUG, "注销NetworkCallback失败: ${e.message}")
        }
    }

    /**
     * 核心逻辑：检查网络状态并发送事件（仅状态变化时发送）
     */
    private fun checkAndPostNetworkState() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // 获取最新网络状态
        val newNetworkAvailable = isNetworkAvailable(connectivityManager)
        val newWifiConnected = isWifiConnected(connectivityManager)

        // 通用网络状态变化 - 仅状态改变时发送事件
        if (newNetworkAvailable != isNetworkAvailable) {
            isNetworkAvailable = newNetworkAvailable
            if (isNetworkAvailable) {
                Log.d(Constants.DEBUG, "网络已连接")
                EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT)
            } else {
                Log.d(Constants.DEBUG, "网络已断开")
                EventBus.getDefault().post(Constants.NETWORK_CONNECTION_FAIL_EVENT)
            }
        }

        // WiFi状态变化 - 仅状态改变时发送事件
        if (newWifiConnected != isWifiConnected) {
            isWifiConnected = newWifiConnected
            if (isWifiConnected) {
                Log.d(Constants.DEBUG, "WiFi已连接")
            } else {
                Log.d(Constants.DEBUG, "WiFi已断开")
                EventBus.getDefault().post(Constants.WIFI_CONNECTION_FAIL_EVENT)
            }
        }
    }

    /**
     * 判断网络是否真正可用
     * 不仅判断连接状态，还验证是否能访问互联网
     */
    private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        // 验证网络具备互联网访问能力且已通过验证
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * 判断当前是否是WiFi连接
     */
    private fun isWifiConnected(connectivityManager: ConnectivityManager): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        // 判断传输类型是否为WiFi
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
    }
}
package com.bll.lnkwrite.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.*
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.manager.*
import com.bll.lnkwrite.mvp.model.*
import com.bll.lnkwrite.ui.adapter.MainListAdapter
import com.bll.lnkwrite.ui.fragment.*
import com.bll.lnkwrite.utils.*
import kotlinx.android.synthetic.main.ac_main.*
import java.io.File
import java.util.*

class MainActivity : BaseActivity(){
    var mainLeftFragment: MainLeftFragment? = null
    var bookcaseFragment: BookcaseFragment? = null
    var documentFragment: DocumentFragment? = null
    var appFragment: AppFragment? = null
    var teachingFragment: TextbookFragment? = null

    var mainRightFragment: MainRightFragment? = null
    var noteFragment: NoteFragment? = null
    var paintingFragment: PaintingFragment? = null
    var screenShotFragment: ScreenShotFragment? = null
    var homeworkFragment: HomeworkManagerFragment? = null

    private var leftPosition = 0
    private var mAdapterLeft: MainListAdapter? = null
    private var leftFragment: Fragment? = null

    private var rightPosition = 0
    private var mAdapterRight: MainListAdapter? = null
    private var rightFragment: Fragment? = null

    private val myBroadcastReceiver=MyBroadcastReceiver()
    private var mTabLefts= mutableListOf<ItemList>()
    private var mTabRights= mutableListOf<ItemList>()

    override fun layoutId(): Int {
        return R.layout.ac_main
    }

    override fun initData() {
        val intentFilter=IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(myBroadcastReceiver,intentFilter)

        val documentPath=FileAddress().getPathDocument(getString(R.string.default_str))
        val childPath= "$documentPath/1"
        if (!FileUtils.isExist(documentPath)){
            FileUtils.mkdirs(childPath)
            MediaScannerConnection.scanFile(this, arrayOf(childPath), null, null)
        }
        Handler().postDelayed({
            FileUtils.delete(childPath)
            MediaScannerConnection.scanFile(this, arrayOf(childPath), null, null)
        }, 10 * 1000)

        val screenshotPath=FileAddress().getPathScreen(getString(R.string.untype))
        if (!FileUtils.isExist(screenshotPath)){
            FileUtils.mkdirs(screenshotPath)
        }

    }

    override fun initView() {
        val isTips=SPUtil.getBoolean("SpecificationTips")
        if (!isTips){
            showView(ll_tips)
        }

        mainLeftFragment = MainLeftFragment()
        bookcaseFragment = BookcaseFragment()
        documentFragment = DocumentFragment()
        appFragment = AppFragment()
        teachingFragment = TextbookFragment()
        noteFragment = NoteFragment()
        paintingFragment = PaintingFragment()
        screenShotFragment = ScreenShotFragment()
        mainRightFragment = MainRightFragment()
        homeworkFragment = HomeworkManagerFragment()

        switchFragment(1, mainLeftFragment)
        switchFragment(2, mainRightFragment)

        mAdapterLeft = MainListAdapter(R.layout.item_main_list, mTabLefts).apply {
            rv_list_a.layoutManager = LinearLayoutManager(this@MainActivity)//创建布局管理
            rv_list_a.adapter = this
            bindToRecyclerView(rv_list_a)
            setOnItemClickListener { adapter, view, position ->
                updateItem(leftPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchFragment(1,mainLeftFragment)//首页
                    1 -> switchFragment(1,bookcaseFragment)//书架
                    2 -> switchFragment(1,documentFragment)//课本
                    3 -> switchFragment(1,appFragment)//义教
                    4 -> switchFragment(1,teachingFragment)//应用
                }
                leftPosition = position
            }
        }

        mAdapterRight = MainListAdapter(R.layout.item_main_list, mTabRights).apply {
            rv_list_b.layoutManager = LinearLayoutManager(this@MainActivity)//创建布局管理
            rv_list_b.adapter = this
            bindToRecyclerView(rv_list_b)
            setOnItemClickListener { adapter, view, position ->
                updateItem(rightPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchFragment(2,  mainRightFragment)
                    1 -> switchFragment(2,  noteFragment)
                    2 -> switchFragment(2,  paintingFragment)
                    3 -> switchFragment(2,  screenShotFragment)
                    4 -> switchFragment(2,  homeworkFragment)
                }
                rightPosition = position
            }
        }

        startRemind()

        iv_user_a.setOnClickListener {
            customStartActivity(Intent(this, AccountInfoActivity::class.java))
        }

        ll_tips.setOnClickListener {
            disMissView(ll_tips)
            SPUtil.putBoolean("SpecificationTips",true)
        }

        refreshData(false)
    }

    private fun switchFragment(type: Int, to: Fragment?) {
        val from = if (type == 1) {
            leftFragment
        } else {
            rightFragment
        }
        if (from != to) {
            if (type == 1) {
                leftFragment = to
            } else {
                rightFragment = to
            }
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(if (type == 1) R.id.frame_layout_a else R.id.frame_layout_b, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    /**
     * 开始每天定时自动刷新
     */
    private fun startRemind() {
        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis

            if (currentTimeMillisLong > selectLong) {
                add(Calendar.DAY_OF_MONTH, 1)
                selectLong = timeInMillis
            }

            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_DAY_REFRESH
            val pendingIntent =PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.SYS_RTC_WAKEUP, selectLong,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
        }
    }


    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.STUDENT_EVENT -> {
                if(DataBeanManager.students.size>0){
                    refreshData(true)
                }
                else{
                    refreshData(false)
                    if (leftPosition>3){
                        leftPosition=0
                        switchFragment(1, mainLeftFragment)
                    }
                    if (rightPosition>3){
                        rightPosition=0
                        switchFragment(2, mainRightFragment)
                    }
                }
                mAdapterLeft?.updateItem(leftPosition,true)
                mAdapterRight?.updateItem(rightPosition,true)
            }
        }
    }

    private fun refreshData(boolean: Boolean){
        mTabLefts=DataBeanManager.getIndexDataLeft(boolean)
        mTabRights=DataBeanManager.getIndexDataRight(boolean)
        mAdapterLeft?.setNewData(mTabLefts)
        mAdapterRight?.setNewData(mTabRights)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_APANEL_BACK || keyCode == KeyEvent.KEYCODE_BPANEL_BACK) {
            false
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myBroadcastReceiver)
    }

}
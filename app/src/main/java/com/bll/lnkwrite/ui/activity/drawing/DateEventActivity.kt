package com.bll.lnkwrite.ui.activity.drawing

import android.os.Handler
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.Constants.dayLong
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseDrawingActivity
import com.bll.lnkwrite.dialog.CalendarSingleDialog
import com.bll.lnkwrite.utils.DateUtils
import kotlinx.android.synthetic.main.common_date_arrow.iv_down
import kotlinx.android.synthetic.main.common_date_arrow.iv_up
import kotlinx.android.synthetic.main.common_date_arrow.tv_date
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateEventActivity:BaseDrawingActivity() {
    private var nowLong=0L

    override fun layoutId(): Int {
        return R.layout.ac_date_event
    }

    override fun initData() {
        nowLong=intent.getLongExtra("date",0)
    }

    override fun initView() {
        MethodManager.setImageResource(this, R.mipmap.icon_date_event_bg,v_content_b)

        setContentView()

        iv_up.setOnClickListener {
            nowLong-=dayLong
            setContentView()
        }

        iv_down.setOnClickListener {
            nowLong+=dayLong
            setContentView()
        }

        tv_date.setOnClickListener {
            CalendarSingleDialog(this,45f,190f).builder().setOnDateListener { dateTim ->
                nowLong=dateTim
                setContentView()
            }
        }

    }

    private fun setContentView(){
        tv_date.text= DateUtils.longToStringWeek(nowLong)
        val path=FileAddress().getPathDate(DateUtils.longToStringCalender(nowLong))+"/draw.png"
        elik_b?.setLoadFilePath(path, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopErasure()
        Handler().postDelayed({
            EventBus.getDefault().post(Constants.DATE_DRAWING_EVENT)
        },100)
    }

}
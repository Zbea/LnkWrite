package com.bll.lnkwrite.ui.fragment

import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.Constants.AUTO_REFRESH_EVENT
import com.bll.lnkwrite.Constants.CALENDER_SET_EVENT
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.manager.CalenderDaoManager
import com.bll.lnkwrite.utils.CalenderUtils
import com.bll.lnkwrite.utils.DateUtils
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.utils.GlideUtils
import com.bll.lnkwrite.utils.NetworkUtil
import com.bll.lnkwrite.utils.date.LunarSolarConverter
import com.bll.lnkwrite.utils.date.Solar
import kotlinx.android.synthetic.main.fragment_main_left.iv_calender
import kotlinx.android.synthetic.main.fragment_main_left.tv_date_day
import kotlinx.android.synthetic.main.fragment_main_left.tv_date_festival
import kotlinx.android.synthetic.main.fragment_main_left.tv_date_luna
import kotlinx.android.synthetic.main.fragment_main_left.tv_date_month
import kotlinx.android.synthetic.main.fragment_main_left.tv_date_week
import kotlinx.android.synthetic.main.fragment_main_left.v_down
import kotlinx.android.synthetic.main.fragment_main_left.v_up
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Random

class MainLeftFragment:BaseFragment(){

    private var nowDayPos=1
    private var nowDay=0L
    private var calenderPath=""

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_left
    }
    override fun initView() {
        setTitle(R.string.home)

        v_up.setOnClickListener{
            nowDay-=Constants.dayLong
            setDateView()
            if (nowDayPos>1){
                nowDayPos-=1
                setCalenderBg()
            }
        }

        v_down.setOnClickListener {
            nowDay+=Constants.dayLong
            setDateView()
            val allDay=if (DateUtils().isYear(DateUtils.getYear())) 366 else 365
            if (nowDayPos<=allDay){
                nowDayPos+=1
                setCalenderBg()
            }
        }

    }
    override fun lazyLoad() {
        onCheckUpdate()
        nowDay=DateUtils.getStartOfDayInMillis()
        setDateView()
        setCalenderView()
    }


    /**
     * 设置当天时间以及图片
     */
    private fun setDateView() {
        tv_date_month.text=SimpleDateFormat("MM").format(nowDay)
        tv_date_day.text=SimpleDateFormat("dd").format(nowDay)
        tv_date_week.text=SimpleDateFormat("EEEE").format(Date(nowDay))

        val dates=DateUtils.getDateNumber(nowDay)
        val solar= Solar()
        solar.solarYear=dates[0]
        solar.solarMonth=dates[1]
        solar.solarDay=dates[2]
        val lunar=LunarSolarConverter.SolarToLunar(solar)

        val str = if (!solar.solar24Term.isNullOrEmpty()) {
            "24节气   "+solar.solar24Term
        } else {
            if (!solar.solarFestivalName.isNullOrEmpty()) {
                "节日   "+solar.solarFestivalName
            } else {
                if (!lunar.lunarFestivalName.isNullOrEmpty()) {
                    "节日   "+lunar.lunarFestivalName
                }
                else{
                    ""
                }
            }
        }
        if (MethodManager.isCN()) {
            tv_date_luna.text=lunar.getChinaMonthString(lunar.lunarMonth)+"月"+lunar.getChinaDayString(lunar.lunarDay)
            tv_date_festival.text=str
        }
    }


    /**
     * 是否显示台历
     */
    private fun setCalenderView(){
        val item=CalenderDaoManager.getInstance().queryCalenderBean()
        if (item!=null){
            calenderPath=item.path
            showView(iv_calender)
            val calenderUtils=CalenderUtils(DateUtils.longToStringDataNoHour(nowDay))
            nowDayPos=calenderUtils.elapsedTime()
            setCalenderBg()
        }
        else{
            disMissView(iv_calender)
        }
    }

    /**
     * 设置台历图片
     */
    private fun setCalenderBg(){
        val listFiles= FileUtils.getFiles(calenderPath)
        if (listFiles.size>0){
            val file=if (listFiles.size>nowDayPos-1){
                listFiles[nowDayPos-1]
            }
            else{
//                listFiles[listFiles.size-1]
                listFiles[Random().nextInt(listFiles.size)]
            }
            GlideUtils.setImageRoundUrl(requireActivity(),file.path,iv_calender,15)
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            CALENDER_SET_EVENT->{
                setCalenderView()
            }
            AUTO_REFRESH_EVENT->{
                lazyLoad()
            }
        }
    }

    override fun onRefreshData() {
        lazyLoad()
    }

}
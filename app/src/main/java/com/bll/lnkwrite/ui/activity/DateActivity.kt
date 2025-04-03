package com.bll.lnkwrite.ui.activity

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.PopupDateSelector
import com.bll.lnkwrite.mvp.model.Date
import com.bll.lnkwrite.ui.activity.drawing.DateEventActivity
import com.bll.lnkwrite.ui.adapter.DateAdapter
import com.bll.lnkwrite.utils.DateUtils
import com.bll.lnkwrite.utils.date.LunarSolarConverter
import com.bll.lnkwrite.utils.date.Solar
import kotlinx.android.synthetic.main.ac_date.*
import kotlinx.android.synthetic.main.common_title.ll_year
import kotlinx.android.synthetic.main.common_title.tv_month
import kotlinx.android.synthetic.main.common_title.tv_year

open class DateActivity: BaseActivity() {

    private var yearPop:PopupDateSelector?=null
    private var monthPop:PopupDateSelector?=null
    private var yearNow=DateUtils.getYear()
    private var monthNow=DateUtils.getMonth()
    private var mAdapter:DateAdapter?=null
    private var dates= mutableListOf<Date>()
    private var position=0
    private var yearList= mutableListOf<Int>()
    private var monthList= mutableListOf<Int>()

    override fun layoutId(): Int {
        return R.layout.ac_date
    }

    override fun initData() {
        val nowYear=DateUtils.getYear()
        for (i in 4 downTo 0){
            yearList.add(nowYear-i)
        }
        for (i in 1..5){
            yearList.add(nowYear+i)
        }

        for (i in 1..12)
        {
            monthList.add(i)
        }
    }

    override fun initView() {
        setPageTitle(R.string.schedule)
        showView(ll_year)

        initRecycler()

        tv_year.text=yearNow.toString()
        tv_month.text=monthNow.toString()

        tv_year.setOnClickListener {
            if (yearPop==null){
                yearPop=PopupDateSelector(this,tv_year,yearList,0).builder()
                yearPop ?.setOnSelectorListener {
                    tv_year.text=it
                    yearNow=it.toInt()
                    Thread{
                        getDates()
                    }.start()
                }
                yearPop?.show()
            }
            else{
                yearPop?.show()
            }
        }

        tv_month.setOnClickListener {
            if (monthPop==null){
                monthPop=PopupDateSelector(this,tv_month,monthList,1).builder()
                monthPop?.setOnSelectorListener {
                    tv_month.text=it
                    monthNow=it.toInt()
                    Thread {
                        getDates()
                    }.start()
                }
                monthPop?.show()
            }
            else{
                monthPop?.show()
            }
        }

        Thread {
            getDates()
        }.start()
    }

    private fun initRecycler(){
        mAdapter = DateAdapter(R.layout.item_date, null)
        rv_list.layoutManager = GridLayoutManager(this,7)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            val dateBean=dates[position]
            if (dateBean.year!=0){
                val intent = Intent(this, DateEventActivity::class.java)
                intent.putExtra("date",dateBean.time)
                customStartActivity(intent)
            }
        }
    }


    //根据月份获取当月日期
    private fun getDates(){
        dates.clear()
//        val lastYear: Int
//        val lastMonth: Int
//        val nextYear: Int
//        val nextMonth: Int
//
//        when (monthNow) {
//            //当月为一月份时候
//            1 -> {
//                lastYear=yearNow-1
//                lastMonth=12
//                nextYear=yearNow
//                nextMonth=monthNow+1
//            }
//            //当月为12月份时候
//            12 -> {
//                lastYear=yearNow
//                lastMonth=monthNow-1
//                nextYear=yearNow+1
//                nextMonth=1
//            }
//            else -> {
//                lastYear=yearNow
//                lastMonth=monthNow-1
//                nextYear=yearNow
//                nextMonth=monthNow+1
//            }
//        }

        var week=DateUtils.getMonthOneDayWeek(yearNow,monthNow-1)
        if (week==1)
            week=8

        //补齐上月差数
        for (i in 0 until week-2){
//            //上月天数
//            val maxDay=DateUtils.getMonthMaxDay(lastYear,lastMonth-1)
//            val day=maxDay-(week-2)+(i+1)
//            dates.add(getDateBean(lastYear,lastMonth,day,false))
            dates.add(Date())
        }

        val max=DateUtils.getMonthMaxDay(yearNow,monthNow-1)
        for (i in 1 .. max)
        {
            dates.add(getDateBean(yearNow,monthNow,i,true))
        }

        if (dates.size>35){
            //补齐下月天数
            for (i in 0 until 42-dates.size){
//                val day=i+1
//                dates.add(getDateBean(nextYear,nextMonth,day,false))
                dates.add(Date())
            }
        }
        else{
            for (i in 0 until 35-dates.size){
//                val day=i+1
//                dates.add(getDateBean(nextYear,nextMonth,day,false))
                dates.add(Date())
            }
        }

        runOnUiThread {
            mAdapter?.setNewData(dates)
        }
    }

    private fun getDateBean(year:Int,month:Int,day:Int,isMonth: Boolean): Date {
        val solar=Solar()
        solar.solarYear=year
        solar.solarMonth=month
        solar.solarDay=day

        val date= Date()
        date.year=year
        date.month=month
        date.day=day
        date.time=DateUtils.dateToStamp("$year-$month-$day")
        date.isNow=day==DateUtils.getDay()&&DateUtils.getMonth()==month
        date.isNowMonth=isMonth
        date.solar= solar
        date.week=DateUtils.getWeek(date.time)
        date.lunar=LunarSolarConverter.SolarToLunar(solar)

        return date
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.DATE_DRAWING_EVENT){
            mAdapter?.notifyItemChanged(position)
        }
    }

}
package com.bll.lnkwrite.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkwrite.R
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.DateUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView


class CalendarSingleDialog(private val context: Context,private val x:Float,private val y:Float) {

    private var dialog: Dialog? = null
    constructor(context: Context):this(context,0f,0f)

    @SuppressLint("SetTextI18n")
    fun builder(): CalendarSingleDialog {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_calendar_single)
        val window = dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        if(x!=0f&&y!=0f){
            val layoutParams = window.attributes
            layoutParams.gravity = Gravity.TOP or Gravity.END
            layoutParams.x = DP2PX.dip2px(context, x)
            layoutParams.y = DP2PX.dip2px(context, y)
        }
        dialog?.show()

        val tv_year = dialog?.findViewById<TextView>(R.id.tv_year)
        val iv_left = dialog?.findViewById<ImageView>(R.id.iv_left)
        val iv_right = dialog?.findViewById<ImageView>(R.id.iv_right)
        val calendarView = dialog?.findViewById<CalendarView>(R.id.dp_date)

        tv_year?.text="${calendarView?.curYear}  -  ${calendarView?.curMonth} "

        iv_left?.setOnClickListener {
            calendarView?.scrollToPre()
        }

        iv_right?.setOnClickListener {
            calendarView?.scrollToNext()
        }

        calendarView?.setOnMonthChangeListener { year, month ->
            tv_year?.text="$year  -  $month "
        }

        calendarView?.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar?) {
            }

            override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                if (isClick){
                    val years = calendar?.year
                    val months = calendar?.month
                    val days = calendar?.day
                    val dateToStamp = "${years}-${months}-${days}"
                    val time = DateUtils.dateToStamp(dateToStamp)
                    dateListener?.getDate(time)
                    dismiss()
                }
            }
        })
        return this
    }


    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private var dateListener: OnDateListener? = null

    fun interface OnDateListener {
        fun getDate(dateTim: Long)
    }

    fun setOnDateListener(dateListener: OnDateListener?) {
        this.dateListener = dateListener
    }


}
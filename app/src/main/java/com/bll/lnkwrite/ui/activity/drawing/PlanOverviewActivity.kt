package com.bll.lnkwrite.ui.activity.drawing


import com.bll.lnkwrite.Constants.weekTime
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseDrawingActivity
import com.bll.lnkwrite.dialog.CalendarSingleDialog
import com.bll.lnkwrite.utils.DateUtils
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_plan_overview.*
import kotlinx.android.synthetic.main.common_date_arrow.iv_down
import kotlinx.android.synthetic.main.common_date_arrow.iv_up
import kotlinx.android.synthetic.main.common_date_arrow.tv_date
import kotlinx.android.synthetic.main.common_drawing_tool.*

class PlanOverviewActivity: BaseDrawingActivity() {

    private var type=1//1月计划 2 周计划
    private var nowYear=0
    private var nowMonth=1
    private var weekStartDate=0L
    private var weekEndDate=0L
    private var posImage = 0
    private var images = mutableListOf<String>()//手写地址

    override fun layoutId(): Int {
        return R.layout.ac_plan_overview
    }

    override fun initData() {
        nowYear=DateUtils.getYear()
        nowMonth=DateUtils.getMonth()

        weekStartDate=DateUtils.getCurrentWeekTimeFrame()[0]
        weekEndDate=DateUtils.getCurrentWeekTimeFrame()[1]
    }
    override fun initView() {
        disMissView(iv_catalog,iv_btn)
        setPageTitle(R.string.plan)

        MethodManager.setImageResource(this,R.mipmap.icon_freenote_bg_1,v_content_b)

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            type = if (i==R.id.rb_month){
                1
            } else{
                2
            }
            setChangeDate()
        }

        setChangeDate()

        iv_up.setOnClickListener {
            if (type==1){
                if (nowMonth==1){
                    nowMonth=12
                    nowYear-=1
                }
                else{
                    nowMonth-=1
                }
            }
            else{
                weekStartDate-=weekTime
                weekEndDate-=weekTime
            }
            setChangeDate()
        }

        iv_down.setOnClickListener {
            if (type==1){
                if (nowMonth==12){
                    nowMonth=1
                    nowYear+=1
                }
                else{
                    nowMonth+=1
                }
            }
            else{
                weekStartDate+=weekTime
                weekEndDate+=weekTime
            }
            setChangeDate()
        }

        tv_date.setOnClickListener {
            CalendarSingleDialog(this,45f,200f).builder().setOnDateListener{
                if (type==1){
                    val dateStr=DateUtils.longToStringDataNoHour(it).split("-")
                    nowYear=dateStr[0].toInt()
                    nowMonth=dateStr[1].toInt()
                }
                else{
                    val weekLong=DateUtils.getCurrentWeekTimeFrame(it)
                    weekStartDate=weekLong[0]
                    weekEndDate=weekLong[1]
                }
                setChangeDate()
            }
        }

    }

    override fun onPageUp() {
        if (posImage>0)
            posImage-=1
        setContentImage()
    }

    override fun onPageDown() {
        posImage+=1
        setContentImage()
    }

    /**
     * 得到文件大小
     */
    private fun getPathsSize(){
        val path=if (type==1){
            FileAddress().getPathPlan(nowYear,nowMonth)
        }
        else{
            FileAddress().getPathPlan(DateUtils.longToString(weekStartDate))
        }
        images.clear()
        if (FileUtils.isExist(path)){
            for (file in FileUtils.getFiles(path)){
                images.add(file.path)
            }
        }
    }

    private fun setChangeDate(){
        if (type==1){
            tv_date.text=nowYear.toString()+" - "+ToolUtils.getFormatNum(nowMonth,"00")
        }
        else{
            tv_date.text= DateUtils.longToStringDataNoYear(weekStartDate)+"~"+DateUtils.longToStringDataNoYear(weekEndDate)
        }
        posImage=0
        getPathsSize()
        setContentImage()
    }

    /**
     * 更换内容
     */
    private fun setContentImage() {
        val path = if (type==1){
            FileAddress().getPathPlan(nowYear,nowMonth)+ "/${posImage + 1}.png"
        }
        else{
            FileAddress().getPathPlan(DateUtils.longToString(weekStartDate))+ "/${posImage + 1}.png"
        }
        //判断路径是否已经创建
        if (!images.contains(path)) {
            images.add(path)
        }
        tv_page.text = "${posImage + 1}"
        tv_page_total.text="${images.size}"
        elik_b?.setLoadFilePath(path, true)
    }
}
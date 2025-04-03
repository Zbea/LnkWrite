package com.bll.lnkwrite.ui.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.dialog.PermissionTimeSelectorDialog
import com.bll.lnkwrite.mvp.model.DateWeek
import com.bll.lnkwrite.mvp.model.PermissionTimeBean
import com.bll.lnkwrite.mvp.model.StudentBean
import com.bll.lnkwrite.mvp.presenter.PermissionSettingPresenter
import com.bll.lnkwrite.mvp.view.IContractView.IPermissionSettingView
import com.bll.lnkwrite.ui.adapter.PermissionTimeAdapter
import kotlinx.android.synthetic.main.ac_student_permission_set.*

class PermissionSettingActivity:BaseActivity(),IPermissionSettingView {

    private var mPresenter=PermissionSettingPresenter(this)
    private var mStudentBean:StudentBean?=null
    private var mBookAdapter:PermissionTimeAdapter?=null
    private var mVideoAdapter:PermissionTimeAdapter?=null
    private var bookTimes= mutableListOf<PermissionTimeBean>()
    private var videoTimes= mutableListOf<PermissionTimeBean>()
    private var type=0
    private var position=0
    private var weekStr=""
    private var startLong=0L
    private var endLong=0L

    override fun onStudent(studentBean: StudentBean) {
        mStudentBean=studentBean

        st_money.isChecked=mStudentBean?.isAllowMoney!!
        st_book.isChecked=mStudentBean?.isAllowBook!!
        st_video.isChecked=mStudentBean?.isAllowVideo!!
        setBookStateView()
        setVideoStateView()

        bookTimes= mStudentBean?.bookList as MutableList<PermissionTimeBean>
        videoTimes= mStudentBean?.videoList as MutableList<PermissionTimeBean>
        mBookAdapter?.setNewData(bookTimes)
        mVideoAdapter?.setNewData(videoTimes)
    }

    override fun onSuccess() {
        mPresenter.onStudent(mStudentBean?.accountId!!)
    }


    override fun onChangeSuccess() {
        when (type) {
            1 -> {
                mStudentBean?.isAllowMoney=!mStudentBean?.isAllowMoney!!
                st_money.isChecked=mStudentBean?.isAllowMoney!!
            }
            2 -> {
                mStudentBean?.isAllowBook=!mStudentBean?.isAllowBook!!
                st_book.isChecked=mStudentBean?.isAllowBook!!
                setBookStateView()
            }
            3 -> {
                mStudentBean?.isAllowVideo=!mStudentBean?.isAllowVideo!!
                st_video.isChecked=mStudentBean?.isAllowVideo!!
                setVideoStateView()
            }
        }
    }

    override fun onEditSuccess() {
        if (type==2){
            val item=bookTimes[position]
            item.startTime=startLong
            item.endTime=endLong
            item.weeks=weekStr
            mBookAdapter?.notifyItemChanged(position)
        }
        else{
            val item=videoTimes[position]
            item.startTime=startLong
            item.endTime=endLong
            item.weeks=weekStr
            mVideoAdapter?.notifyItemChanged(position)
        }
    }


    override fun layoutId(): Int {
        return R.layout.ac_student_permission_set
    }

    override fun initData() {
        mStudentBean = intent.getBundleExtra("bundle")?.getSerializable("studentInfo") as StudentBean
        bookTimes= mStudentBean?.bookList as MutableList<PermissionTimeBean>
        videoTimes= mStudentBean?.videoList as MutableList<PermissionTimeBean>
        mPresenter.onStudent(mStudentBean?.accountId!!)
    }

    override fun initView() {
        setPageTitle(mStudentBean?.nickname+"    "+getString(R.string.permission_setting))

        st_money.setOnClickListener {
            val titleStr=if (mStudentBean?.isAllowMoney!!) getString(R.string.tips_unallow_play_qd) else getString(R.string.tips_allow_play_qd)
            CommonDialog(this).setContent(titleStr).builder().onDialogClickListener= object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    type=1
                    val map=HashMap<String,Any>()
                    map["accountId"]=mStudentBean?.accountId!!
                    map["buyState"]=if (mStudentBean?.isAllowMoney!!) 2 else 1
                    mPresenter.onChangeAllow(map)
                }
            }
        }

        st_book.setOnClickListener {
            type=2
            val map=HashMap<String,Any>()
            map["accountId"]=mStudentBean?.accountId!!
            map["bookState"]=if (mStudentBean?.isAllowBook!!) 2 else 1
            mPresenter.onChangeAllow(map)
        }

        st_video.setOnClickListener {
            type=3
            val map=HashMap<String,Any>()
            map["accountId"]=mStudentBean?.accountId!!
            map["videoState"]=if (mStudentBean?.isAllowVideo!!) 2 else 1
            mPresenter.onChangeAllow(map)
        }

        iv_book_add.setOnClickListener {
            PermissionTimeSelectorDialog(this, getWeeks(1)).builder().setOnDateListener{
                startLon,endLon,weeks->
                val map=HashMap<String,Any>()
                map["type"]=1
                map["startTime"]=startLon
                map["endTime"]=endLon
                map["userId"]=mStudentBean?.accountId!!
                map["weeks"]=getWeekStr(weeks)
                mPresenter.onInsertTime(map)
            }
        }

        iv_video_add.setOnClickListener {
            PermissionTimeSelectorDialog(this, getWeeks(2)).builder().setOnDateListener{
                    startLon,endLon,weeks->
                val map=HashMap<String,Any>()
                map["type"]=2
                map["startTime"]=startLon
                map["endTime"]=endLon
                map["userId"]=mStudentBean?.accountId!!
                map["weeks"]=getWeekStr(weeks)
                mPresenter.onInsertTime(map)
            }
        }

        rv_book_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mBookAdapter = PermissionTimeAdapter(R.layout.item_permission_time, bookTimes).apply {
            rv_book_list.adapter = this
            bindToRecyclerView(rv_book_list)
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.iv_delete){
                    val map=HashMap<String,Any>()
                    map["id"]=bookTimes[position].id
                    map["userId"]=mStudentBean?.accountId!!
                    mPresenter.onDeleteTime(map)
                }
            }
            setOnItemClickListener { adapter, view, position ->
                this@PermissionSettingActivity.position=position
                type=2
                val item=bookTimes[position]
                PermissionTimeSelectorDialog(this@PermissionSettingActivity, getWeeks(1),item).builder().setOnDateListener{
                        startLon,endLon,weeks->
                    startLong=startLon
                    endLong=endLon
                    weekStr=getWeekStr(weeks)
                    val map=HashMap<String,Any>()
                    map["type"]=1
                    map["startTime"]=startLon
                    map["endTime"]=endLon
                    map["userId"]=mStudentBean?.accountId!!
                    map["weeks"]=weekStr
                    map["id"]=item.id
                    mPresenter.onEditTime(map)
                }
            }
        }


        rv_video_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mVideoAdapter = PermissionTimeAdapter(R.layout.item_permission_time, videoTimes).apply {
            rv_video_list.adapter = this
            bindToRecyclerView(rv_video_list)
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.iv_delete){
                    val map=HashMap<String,Any>()
                    map["id"]=videoTimes[position].id
                    map["userId"]=mStudentBean?.accountId!!
                    mPresenter.onDeleteTime(map)
                }
            }
            setOnItemClickListener { adapter, view, position ->
                this@PermissionSettingActivity.position=position
                type=3
                val item=videoTimes[position]
                PermissionTimeSelectorDialog(this@PermissionSettingActivity, getWeeks(1),item).builder().setOnDateListener{
                        startLon,endLon,weeks->
                    startLong=startLon
                    endLong=endLon
                    weekStr=getWeekStr(weeks)
                    val map=HashMap<String,Any>()
                    map["type"]=2
                    map["startTime"]=startLon
                    map["endTime"]=endLon
                    map["userId"]=mStudentBean?.accountId!!
                    map["weeks"]=weekStr
                    map["id"]=item.id
                    mPresenter.onEditTime(map)
                }
            }
        }

    }

    private fun getWeeks(type:Int):List<Int>{
        val weeks= mutableListOf<Int>()
        val list=if (type==1) bookTimes else videoTimes
        for (item in list){
            val week=item.weeks.split(",")
            for (i in week){
                weeks.add(i.toInt())
            }
        }
        return weeks
    }

    private fun getWeekStr(weeks:List<DateWeek>):String{
        var week=""
        for (i in weeks.indices){
            week += if (i == weeks.size - 1) {
                "${weeks[i].week}"
            } else {
                "${weeks[i].week},"
            }
        }
        return week
    }

    private fun setBookStateView(){
        iv_book_add.visibility=if (mStudentBean?.isAllowBook!!) View.VISIBLE else View.GONE
        rv_book_list.visibility=if (mStudentBean?.isAllowBook!!) View.VISIBLE else View.GONE
    }

    private fun setVideoStateView(){
        iv_video_add.visibility=if (mStudentBean?.isAllowVideo!!) View.VISIBLE else View.GONE
        rv_video_list.visibility=if (mStudentBean?.isAllowVideo!!) View.VISIBLE else View.GONE
    }

}
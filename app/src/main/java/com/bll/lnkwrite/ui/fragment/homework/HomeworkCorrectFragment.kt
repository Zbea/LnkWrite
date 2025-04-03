package com.bll.lnkwrite.ui.fragment.homework

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.mvp.model.teaching.HomeworkCorrectList
import com.bll.lnkwrite.mvp.model.teaching.HomeworkCorrectList.CorrectBean
import com.bll.lnkwrite.mvp.presenter.HomeworkCorrectPresenter
import com.bll.lnkwrite.mvp.view.IContractView.IHomeworkCorrectView
import com.bll.lnkwrite.ui.activity.drawing.HomeworkCorrectActivity
import com.bll.lnkwrite.ui.adapter.HomeworkCorrectAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.NetworkUtil
import com.bll.lnkwrite.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.*

class HomeworkCorrectFragment:BaseFragment(),IHomeworkCorrectView {

    private val mPresenter=HomeworkCorrectPresenter(this,2)
    private var mAdapter:HomeworkCorrectAdapter?=null
    private var homeworks= mutableListOf<CorrectBean>()
    private var studentId=0
    private var position=0

    override fun onList(list: HomeworkCorrectList?) {
        setPageNumber(list?.total!!)
        homeworks=list.list
        mAdapter?.setNewData(homeworks)
    }
    override fun onDeleteSuccess() {
        showToast(R.string.delete_success)
        fetchData()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=6
        initDialog(2)

        initRecyclerView()
        if(DataBeanManager.students.size>0)
            studentId= DataBeanManager.students[0].accountId
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected())
            fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),50f), DP2PX.dip2px(requireActivity(),20f),
            DP2PX.dip2px(requireActivity(),50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mAdapter = HomeworkCorrectAdapter(R.layout.item_homework_teacher, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceItemDeco(40))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            val item=homeworks[position]
            if (item.status!=1){
                val intent= Intent(requireActivity(), HomeworkCorrectActivity::class.java)
                val bundle= Bundle()
                bundle.putSerializable("correctBean", item)
                intent.putExtra("bundle", bundle)
                customStartActivity(intent)
            }
            else{
                showToast("学生作业未提交")
            }
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.iv_delete){
                val map=HashMap<String,Any>()
                map["ids"]= arrayOf(homeworks[position].id)
                mPresenter.deleteCorrect(map)
            }
        }
    }

    fun onChangeStudent(id:Int){
        pageIndex=1
        studentId=id
        fetchData()
    }

    override fun onRefreshData() {
        lazyLoad()
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=6
        map["childId"]=studentId
        mPresenter.getCorrects(map)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT ->{
                lazyLoad()
            }
            Constants.HOMEWORK_CORRECT_EVENT->{
                fetchData()
            }
        }
    }
}
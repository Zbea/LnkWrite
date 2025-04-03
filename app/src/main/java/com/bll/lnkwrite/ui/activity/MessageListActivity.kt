package com.bll.lnkwrite.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.Constants.MESSAGE_EVENT
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.MessageSendDialog
import com.bll.lnkwrite.dialog.PopupRadioList
import com.bll.lnkwrite.mvp.model.MessageList
import com.bll.lnkwrite.mvp.presenter.MessagePresenter
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.ui.adapter.MessageAdapter
import com.bll.lnkwrite.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class MessageListActivity:BaseActivity(),IContractView.IMessageView {

    private var mMessagePresenter= MessagePresenter(this,getCurrentScreenPos())
    private var messages= mutableListOf<MessageList.MessageBean>()
    private var mAdapter:MessageAdapter?=null
    private var studentId=0

    override fun onList(message: MessageList) {
        setPageNumber(message.total)
        messages=message.list
        mAdapter?.setNewData(messages)
    }

    override fun onCommitSuccess() {
        EventBus.getDefault().post(MESSAGE_EVENT)
        pageIndex=1
        fetchData()
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
        studentId=DataBeanManager.students[0].accountId
        fetchData()
    }

    override fun initView() {
        setPageTitle(R.string.message)
        showView(tv_ok)
        tv_ok.setText(R.string.send)
        if (DataBeanManager.students.size>1){
            showView(tv_type)
            tv_type.text=DataBeanManager.students[0].nickname
        }

        tv_ok.setOnClickListener {
            MessageSendDialog(this).builder().setOnClickListener{
                val map=HashMap<String,Any>()
                map["title"]=it
                map["userId"]=studentId
                mMessagePresenter.commitMessage(map)
            }
        }

        tv_type.setOnClickListener {
            PopupRadioList(this, DataBeanManager.popupStudents, tv_type, tv_type.width, 10).builder()
                .setOnSelectListener {
                    studentId=it.id
                    tv_type.text = it.name
                    pageIndex=1
                    fetchData()
                }
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = MessageAdapter(R.layout.item_message, null).apply {
            val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(
                DP2PX.dip2px(this@MessageListActivity,50f),
                DP2PX.dip2px(this@MessageListActivity,30f),
                DP2PX.dip2px(this@MessageListActivity,50f),20)
            layoutParams.weight=1f
            rv_list.layoutParams= layoutParams
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
        }
    }

    override fun fetchData() {
        val map= HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["userId"]=studentId
        mMessagePresenter.getList(map)
    }

    override fun onRefreshData() {
        fetchData()
    }
}
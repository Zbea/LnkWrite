package com.bll.lnkwrite.ui.fragment.homework

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.HomeworkPublishDialog
import com.bll.lnkwrite.dialog.InputContentDialog
import com.bll.lnkwrite.dialog.LongClickManageDialog
import com.bll.lnkwrite.mvp.model.teaching.HomeworkTypeList
import com.bll.lnkwrite.mvp.model.ItemList
import com.bll.lnkwrite.mvp.presenter.MyHomeworkPresenter
import com.bll.lnkwrite.mvp.view.IContractView.IMyHomeworkView
import com.bll.lnkwrite.ui.adapter.HomeworkTypeAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.NetworkUtil
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.*

class MyHomeworkFragment:BaseFragment(),IMyHomeworkView {

    private var presenter=MyHomeworkPresenter(this,3)
    private var studentId=0
    private var homeworkTypes= mutableListOf<HomeworkTypeList.HomeworkTypeBean>()
    private var mAdapter:HomeworkTypeAdapter?=null
    private var position=0
    private var editNameStr=""

    override fun onList(homeworkTypeList: HomeworkTypeList) {
        setPageNumber(homeworkTypeList.total)
        homeworkTypes=homeworkTypeList.list
        mAdapter?.setNewData(homeworkTypes)
    }
    override fun onCreateSuccess() {
        pageIndex=1
        fetchData()
    }

    override fun onEditSuccess() {
        homeworkTypes[position].name=editNameStr
        mAdapter?.notifyItemChanged(position)
    }

    override fun onDelete() {
        mAdapter?.remove(position)
    }

    override fun onSendSuccess() {
        showToast(R.string.assign_success)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }
    override fun initView() {
        pageSize=9
        initDialog(2)
        if (DataBeanManager.students.size>0)
            studentId=DataBeanManager.students[0].accountId

        initRecyclerView()
    }
    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected())
            fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(requireActivity(),30f), DP2PX.dip2px(requireActivity(),30f), DP2PX.dip2px(requireActivity(),30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = HomeworkTypeAdapter(R.layout.item_homework, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 3)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(3, 85))
            setOnItemClickListener { adapter, view, position ->
                sendHomework(homeworkTypes[position])
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@MyHomeworkFragment.position=position
                onLongClick()
                true
            }
        }
    }

    private fun onLongClick(){
        val item=homeworkTypes[position]
        val beans = mutableListOf<ItemList>()
        beans.add(ItemList().apply {
            name = getString(R.string.delete)
            resId = R.mipmap.icon_setting_delete
        })
        beans.add(ItemList().apply {
            name = getString(R.string.rename)
            resId = R.mipmap.icon_setting_edit
        })
        LongClickManageDialog(requireActivity(),2,item.name, beans).builder()
            .setOnDialogClickListener { position->
                when(position){
                    0->{
                        val map=HashMap<String,Any>()
                        map["ids"]= arrayOf(item.id)
                        presenter.deleteHomeworkType(map)
                    }
                    1->{
                        InputContentDialog(requireActivity(),item.name).builder().setOnDialogClickListener{
                            editNameStr=it
                            val map=HashMap<String,Any>()
                            map["id"]= item.id
                            map["name"]= it
                            presenter.editHomeworkType(map)
                        }
                    }
                }
            }
    }

    /**
     * 布置作业
     */
    private fun sendHomework(item: HomeworkTypeList.HomeworkTypeBean){
        HomeworkPublishDialog(requireActivity()).builder().setOnDialogClickListener{
            content,date->
            val map=HashMap<String,Any>()
            map["id"]=item.id
            map["title"]=content
            map["endTime"]=date
            presenter.sendHomework(map)
        }
    }

    /**
     * 创建作业本
     */
    fun createHomeworkType(name:String,courseId:Int){
        val map=HashMap<String,Any>()
        map["name"]=name
        map["subject"]=courseId
        map["type"]=1
        map["childId"]=studentId
        presenter.createHomeworkType(map)
    }

    fun onChangeStudent(id:Int){
        studentId=id
        pageIndex=1
        fetchData()
    }

    override fun onRefreshData() {
        lazyLoad()
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["size"]=pageSize
        map["page"]=pageIndex
        map["childId"]=studentId
        presenter.getHomeworks(map)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT ->{
                lazyLoad()
            }
        }
    }

}
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
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.mvp.model.TeacherHomeworkList
import com.bll.lnkwrite.mvp.model.TeacherHomeworkList.TeacherHomeworkBean
import com.bll.lnkwrite.mvp.presenter.HomeworkPresenter
import com.bll.lnkwrite.mvp.view.IContractView.IHomeworkView
import com.bll.lnkwrite.ui.activity.ScoreActivity
import com.bll.lnkwrite.ui.activity.drawing.HomeworkDetailsActivity
import com.bll.lnkwrite.ui.adapter.TeacherHomeworkAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.NetworkUtil
import com.bll.lnkwrite.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.*

class HomeworkFragment : BaseFragment(),IHomeworkView {

    private var index=0//1作业2考卷
    private val presenter=HomeworkPresenter(this,2)
    private var homeworks = mutableListOf<TeacherHomeworkBean>()
    private var mAdapterHomework: TeacherHomeworkAdapter? = null
    private var studentId = 0

    private var courseStr=""
    private var position=0

    /**
     * 实例 传送数据
     */
    fun newInstance(index:Int): HomeworkFragment {
        val fragment= HomeworkFragment()
        val bundle= Bundle()
        bundle.putInt("index",index)
        fragment.arguments=bundle
        return fragment
    }

    override fun onList(item: TeacherHomeworkList) {
        setPageNumber(item.total)
        homeworks=item.list
        mAdapterHomework?.setNewData(homeworks)
    }
    override fun onDeleteSuccess() {
        mAdapterHomework?.remove(position)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        index= arguments?.getInt("index")!!
        pageSize=6
        initDialog(2)

        initRecyclerView()

        if(DataBeanManager.students.size>0)
            studentId=DataBeanManager.students[0].accountId
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
        mAdapterHomework = TeacherHomeworkAdapter(R.layout.item_homework_teacher, null,index)
        rv_list.adapter = mAdapterHomework
        mAdapterHomework?.bindToRecyclerView(rv_list)
        mAdapterHomework?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceItemDeco( 40))
        mAdapterHomework?.setOnItemClickListener { adapter, view, position ->
            val item=homeworks[position]
            val intent= Intent(requireActivity(), HomeworkDetailsActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("homeworkBean", item)
            intent.putExtra("bundle", bundle)
            customStartActivity(intent)
        }
        mAdapterHomework?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            val item=homeworks[position]
            when(view.id){
                R.id.iv_delete->{
                    CommonDialog(requireActivity(),2).setContent(R.string.tips_is_delete).builder().setDialogClickListener(
                        object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                val map=HashMap<String,Any>()
                                map["ids"]=arrayOf(item.id)
                                presenter.deleteHomeworks(map)
                            }
                        })
                }
                R.id.iv_rank->{
                    customStartActivity(Intent(requireActivity(),ScoreActivity::class.java).setFlags(index).putExtra("id",item.studentTaskId))
                }
            }
        }
    }


    fun onChangeStudent(id:Int){
        pageIndex=1
        studentId=id
        fetchData()
    }

    fun onChangeCourse(coures:String){
        courseStr=coures
        pageIndex=1
        fetchData()
    }

    override fun onRefreshData() {
        courseStr=""
        lazyLoad()
    }

    override fun fetchData() {
        homeworks.clear()
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["childId"]=studentId
        map["type"] =index
        if (courseStr.isNotEmpty())
            map["subject"]=courseStr
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
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
import com.bll.lnkwrite.dialog.ImageDialog
import com.bll.lnkwrite.mvp.model.teaching.ExamList
import com.bll.lnkwrite.mvp.presenter.ExamPresenter
import com.bll.lnkwrite.mvp.view.IContractView.IExamView
import com.bll.lnkwrite.ui.activity.ScoreActivity
import com.bll.lnkwrite.ui.activity.drawing.ExamDetailsActivity
import com.bll.lnkwrite.ui.adapter.ExamAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.NetworkUtil
import com.bll.lnkwrite.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.*

class ExamFragment : BaseFragment(),IExamView {
    private val presenter=ExamPresenter(this,2)
    private var exams = mutableListOf<ExamList.ExamBean>()
    private var mAdapter: ExamAdapter? = null
    private var studentId = 0
    private var courseStr=""
    private var position=0

    override fun onList(item: ExamList) {
        setPageNumber(item.total)
        exams=item.list
        mAdapter?.setNewData(exams)
    }

    override fun onDeleteSuccess() {
        mAdapter?.remove(position)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
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
        mAdapter = ExamAdapter(R.layout.item_homework_teacher, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceItemDeco( 50))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val item=exams[position]
            val intent= Intent(requireActivity(), ExamDetailsActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("examBean", item)
            intent.putExtra("bundle", bundle)
            customStartActivity(intent)
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            val item=exams[position]
            when(view.id){
                R.id.tv_rank->{
                    customStartActivity(Intent(requireActivity(),ScoreActivity::class.java)
                        .setFlags(0)
                        .putExtra("id",item.schoolExamJobId)
                    )
                }
                R.id.iv_delete->{
                    CommonDialog(requireActivity(),2).setContent(R.string.tips_is_delete).builder().setDialogClickListener(
                        object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                val map=HashMap<String,Any>()
                                map["id"]=item.id
                                presenter.deleteExam(map)
                            }
                        })
                }
                R.id.tv_answer->{
                    val images=item.answerUrl.split(",").toList()
                    ImageDialog(requireActivity(),2,images).builder()
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
        exams.clear()
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["childId"]=studentId
        if (courseStr.isNotEmpty())
            map["type"]=DataBeanManager.getCourseId(courseStr)
        presenter.getExams(map)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT ->{
                lazyLoad()
            }
        }
    }

}
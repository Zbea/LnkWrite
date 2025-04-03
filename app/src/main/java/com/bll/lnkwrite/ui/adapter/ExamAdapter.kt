package com.bll.lnkwrite.ui.adapter

import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.teaching.ExamList
import com.bll.lnkwrite.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamAdapter(layoutResId: Int, data: List<ExamList.ExamBean>?) : BaseQuickAdapter<ExamList.ExamBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExamList.ExamBean) {
        helper.apply {
            setText(R.id.tv_status,DataBeanManager.getCourseStr(item.subject))
            setGone(R.id.tv_type,false)
            setText(R.id.tv_content,item.examName)
            setText(R.id.tv_commitTime,"考试提交时间："+DateUtils.longToStringWeek(item.expTime))
            setText(R.id.tv_correctTime,"批改下发时间："+DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime)))
            setText(R.id.tv_startTime,if(DateUtils.dateStrToLong(item.startTime)<=0L)"" else "布置时间："+DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.startTime)))
            addOnClickListener(R.id.iv_delete,R.id.iv_rank)
        }
    }

}

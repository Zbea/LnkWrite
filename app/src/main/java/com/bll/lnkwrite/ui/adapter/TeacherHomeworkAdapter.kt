package com.bll.lnkwrite.ui.adapter

import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.TeacherHomeworkList.TeacherHomeworkBean
import com.bll.lnkwrite.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TeacherHomeworkAdapter(layoutResId: Int, data: List<TeacherHomeworkBean>?,val type:Int) : BaseQuickAdapter<TeacherHomeworkBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TeacherHomeworkBean) {
        helper.apply {
            setText(R.id.tv_status,"${item.subject}  ${if(type==1) when (item.status){ 1-> getSting(R.string.notice) 2-> getSting(R.string.commit) else ->getSting(R.string.correct)} else ""}")
            setText(R.id.tv_type,item.homeworkName)
            setText(R.id.tv_content,item.title+"  "+if (item.submitTime==0L)"" else DateUtils.longToStringWeek(item.submitTime)+"提交")
            setText(R.id.tv_commitTime,if (item.status==1)"" else "学生提交时间："+DateUtils.longToStringWeek(item.time))
            setText(R.id.tv_startTime, "布置时间："+DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime)))
            setText(R.id.tv_correctTime, if(DateUtils.dateStrToLong(item.correctTime)<=0L)"" else "批改下发时间："+DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.correctTime)))
            setGone(R.id.iv_rank,item.type==2)

            addOnClickListener(R.id.iv_delete,R.id.iv_rank)
        }
    }

    private fun getSting(resId:Int):String{
        return mContext.getString(resId)
    }

}

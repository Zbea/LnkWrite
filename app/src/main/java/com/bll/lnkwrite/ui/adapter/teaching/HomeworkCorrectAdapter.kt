package com.bll.lnkwrite.ui.adapter.teaching

import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.teaching.HomeworkCorrectList
import com.bll.lnkwrite.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkCorrectAdapter(layoutResId: Int, data: List<HomeworkCorrectList.CorrectBean>?) : BaseQuickAdapter<HomeworkCorrectList.CorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkCorrectList.CorrectBean) {
        helper.apply {
            setText(R.id.tv_status,"${DataBeanManager.getCourseStr(item.subject)}    ${when (item.status){ 1-> "通知" 2-> "提交" else ->"批改"}}")
            setText(R.id.tv_type,item.homeworkName)
            setText(R.id.tv_content,item.content)
            setText(R.id.tv_commitTime,if (item.status==1)"提交截止："+DateUtils.longToStringWeek(item.endTime) else "提交时间："+DateUtils.longToStringWeek(item.submitTime))
            setText(R.id.tv_startTime, "布置时间："+DateUtils.longToStringWeek(item.time))
            setGone(R.id.iv_rank,false)

            addOnClickListener(R.id.iv_delete)
        }
    }

}

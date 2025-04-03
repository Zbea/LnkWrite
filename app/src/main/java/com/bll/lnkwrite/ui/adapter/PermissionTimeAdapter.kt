package com.bll.lnkwrite.ui.adapter

import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.PermissionTimeBean
import com.bll.lnkwrite.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PermissionTimeAdapter(layoutResId: Int, data: List<PermissionTimeBean>?) : BaseQuickAdapter<PermissionTimeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: PermissionTimeBean) {
        helper.apply {
            setText(R.id.tv_time,DateUtils.longToHour(DateUtils.getStartOfDayInMillis()+item.startTime)
            +"~"+DateUtils.longToHour(DateUtils.getStartOfDayInMillis()+item.endTime)
            )
            var weekStr=""
            for (i in item.weeks.split(",")){
               weekStr=weekStr+DataBeanManager.getWeekStr(i.toInt())+"  "
            }
            setText(R.id.tv_week, "$weekStr 不能查看")
            addOnClickListener(R.id.iv_delete)
        }
    }

}

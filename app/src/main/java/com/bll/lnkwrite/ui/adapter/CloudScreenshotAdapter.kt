package com.bll.lnkwrite.ui.adapter

import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudScreenshotAdapter(layoutResId: Int, data: List<ItemTypeBean>?) : BaseQuickAdapter<ItemTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemTypeBean) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, DateUtils.longToStringDataNoHour(item.date))
        helper.addOnClickListener(R.id.iv_delete)
    }

}

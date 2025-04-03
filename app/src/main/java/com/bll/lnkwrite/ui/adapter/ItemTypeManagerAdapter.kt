package com.bll.lnkwrite.ui.adapter

import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ItemTypeManagerAdapter(layoutResId: Int, data: List<ItemTypeBean>?) : BaseQuickAdapter<ItemTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemTypeBean) {
        helper.setText(R.id.tv_name,item.title)
        helper.setGone(R.id.iv_upload,item.type==3)
        helper.addOnClickListener(R.id.iv_edit,R.id.iv_delete,R.id.iv_top,R.id.iv_upload)
    }

}

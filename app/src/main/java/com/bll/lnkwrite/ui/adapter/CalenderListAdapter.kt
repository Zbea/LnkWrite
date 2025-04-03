package com.bll.lnkwrite.ui.adapter

import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.CalenderItemBean
import com.bll.lnkwrite.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CalenderListAdapter(layoutResId: Int,data: List<CalenderItemBean>?) : BaseQuickAdapter<CalenderItemBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CalenderItemBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            GlideUtils.setImageRoundUrl(mContext,item.imageUrl,getView(R.id.iv_image),8)
            setText(R.id.tv_buy,if (item.buyStatus==1) getString(R.string.download) else getString(R.string.buy))
            addOnClickListener(R.id.tv_buy)
        }
    }
    fun getString(resId:Int):String{
        return mContext.getString(resId)
    }
}

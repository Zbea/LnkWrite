package com.bll.lnkwrite.ui.adapter

import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.WallpaperBean
import com.bll.lnkwrite.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class WallpaperAdapter(layoutResId: Int,data: List<WallpaperBean>?) : BaseQuickAdapter<WallpaperBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: WallpaperBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            setText(R.id.tv_price,if (item.price==0) getString(R.string.free) else item.price.toString()+getString(R.string.xd))
            setText(R.id.btn_download,if (item.buyStatus==1) getString(R.string.download) else getString(R.string.buy))
            GlideUtils.setImageRoundUrl(mContext,item.bodyUrl.split(",")[0],getView(R.id.iv_image_left),8)
            GlideUtils.setImageRoundUrl(mContext,item.bodyUrl.split(",")[1],getView(R.id.iv_image_right),8)
            addOnClickListener(R.id.btn_download)
        }

    }

    fun getString(resId:Int):String{
        return mContext.getString(resId)
    }

}

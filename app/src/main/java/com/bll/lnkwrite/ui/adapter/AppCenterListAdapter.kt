package com.bll.lnkwrite.ui.adapter

import android.widget.ImageView
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.AppList
import com.bll.lnkwrite.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppCenterListAdapter(layoutResId: Int, data: List<AppList.ListBean>?) : BaseQuickAdapter<AppList.ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppList.ListBean) {
        helper.apply {
            setText(R.id.tv_name,item.nickname)
            setText(R.id.tv_introduce,item.introduction)
            setText(R.id.btn_download,if (item.buyStatus==1) getString(R.string.download) else getString(R.string.buy))
            setText(R.id.tv_price,if (item.price==0) getString(R.string.free) else item.price.toString())
            setGone(R.id.tv_price_title,item.price!=0)
            val image=getView<ImageView>(R.id.iv_image)
            GlideUtils.setImageRoundUrl(mContext,item.assetUrl,image,5)
        }
    }

    fun getString(resId:Int):String{
        return mContext.getString(resId)
    }

}

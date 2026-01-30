package com.bll.lnkwrite.ui.adapter

import android.widget.ImageView
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.CalenderItemBean
import com.bll.lnkwrite.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CalenderListAdapter(layoutResId: Int,data: List<CalenderItemBean>?) : BaseQuickAdapter<CalenderItemBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CalenderItemBean) {
        helper.apply {
            setText(R.id.tv_name, item.title)
            val image = getView<ImageView>(R.id.iv_image)
            GlideUtils.setImageRoundUrl(mContext, item.imageUrl, image, 8)
            when(item.loadSate){
                2->{
                    setText(R.id.tv_buy,getString(R.string.downloaded))
                }
                1->{
                    setText(R.id.tv_buy,item.loadString)
                }
                0->{
                    setText(R.id.tv_buy,if (item.buyStatus==1) getString(R.string.download) else getString(R.string.buy))
                }
            }

            setText(R.id.tv_price,if (item.price==0) getString(R.string.free) else "${item.price}")

            addOnClickListener(R.id.tv_buy)
        }
    }

    private fun getString(resId:Int):String{
        return mContext.getString(resId)
    }

    fun setChangeText(s:String,pos:Int){
        data[pos].loadString=s
        data[pos].loadSate=1
        notifyItemChanged(pos)
    }

    fun setInitText(pos: Int){
        data[pos].loadString=""
        data[pos].loadSate=0
        notifyItemChanged(pos)
    }
}

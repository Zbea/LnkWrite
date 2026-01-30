package com.bll.lnkwrite.ui.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.book.TextbookBean
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TextBookStoreAdapter(layoutResId: Int, data: List<TextbookBean>?) : BaseQuickAdapter<TextbookBean, BaseViewHolder>(layoutResId, data) {
    var type=0

    override fun convert(helper: BaseViewHolder, item: TextbookBean) {
        helper.apply {
            setText(R.id.tv_name, item.bookName)
            val image = getView<ImageView>(R.id.iv_image)
            GlideUtils.setImageRoundUrl(mContext, item.imageUrl, image, 8)
            when(item.loadSate){
                2->{
                    setText(R.id.tv_buy,getString(R.string.open))
                }
                1->{
                    setText(R.id.tv_buy,item.loadString)
                }
                0->{
                    setText(R.id.tv_buy,if (item.buyStatus==1) getString(R.string.download) else getString(R.string.buy))
                }
            }

            val tvPrice=getView<TextView>(R.id.tv_price)
            when(type){
                0->{
                    toggleDrawableLeft(tvPrice,false)
                    setText(R.id.tv_price, DataBeanManager.getBookVersionStr(item.version))
                }
                else->{
                    toggleDrawableLeft(tvPrice,true)
                    setText(R.id.tv_price,if (item.price==0) " ${getString(R.string.free)}" else " ${item.price}")
                }
            }

            addOnClickListener(R.id.tv_buy)
        }
    }

    private fun getString(resId:Int):String{
        return mContext.getString(resId)
    }

    // 快速切换显示/隐藏（复用缓存的 Drawable）
    private fun toggleDrawableLeft(view: TextView, show: Boolean) {
        if (show) {
            val leftDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_wallet_smoney)
            val drawableSize = DP2PX.dip2px(mContext, 20f)
            leftDrawable?.setBounds(0, 0, drawableSize, drawableSize)
            view.setCompoundDrawables(leftDrawable, null, null, null)
        } else {
            view.setCompoundDrawables(null, null, null, null)
        }
    }

    fun setChangeType(type:Int){
        this.type=type
        notifyDataSetChanged()
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

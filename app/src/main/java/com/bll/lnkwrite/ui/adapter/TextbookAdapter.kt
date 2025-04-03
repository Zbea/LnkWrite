package com.bll.lnkwrite.ui.adapter

import android.widget.ImageView
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.book.TextbookBean
import com.bll.lnkwrite.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TextbookAdapter(layoutResId: Int, data: List<TextbookBean>?) : BaseQuickAdapter<TextbookBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: TextbookBean) {
        helper.setText(R.id.tv_name, item.bookName)
        val image = helper.getView<ImageView>(R.id.iv_image)
        if (item.pageUrl.isNullOrEmpty()) {
            GlideUtils.setImageRoundUrl(mContext, item.imageUrl, image, 8)
        } else {
            GlideUtils.setImageRoundUrl(mContext, item.pageUrl, image, 8)
        }
    }

}

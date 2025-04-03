package com.bll.lnkwrite.ui.adapter

import android.widget.ImageView
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.book.Book
import com.bll.lnkwrite.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookAdapter(layoutResId: Int, data: List<Book>?) : BaseQuickAdapter<Book, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Book) {
        helper.apply {
            setText(R.id.tv_name,item.bookName)
            val image=getView<ImageView>(R.id.iv_image)
            if(item.pageUrl.isNullOrEmpty())
            {
                GlideUtils.setImageRoundUrl(mContext,item.imageUrl,image,10)
            }
            else{
                GlideUtils.setImageRoundUrl(mContext,item.pageUrl,image,10)
            }
        }
    }

}

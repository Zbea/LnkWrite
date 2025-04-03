package com.bll.lnkwrite.ui.adapter

import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.Note
import com.bll.lnkwrite.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NoteAdapter(layoutResId: Int, data: List<Note>?) : BaseQuickAdapter<Note, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Note) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, DateUtils.longToStringDataNoHour(item.date))
        helper.setVisible(R.id.iv_password,item.typeStr==mContext.getString(R.string.note_tab_diary))
        if (MethodManager.getPrivacyPassword(1)!=null)
            helper.setImageResource(R.id.iv_password,if (item.isCancelPassword) R.mipmap.icon_encrypt_cancel else R.mipmap.icon_encrypt_check)
        helper.addOnClickListener(R.id.iv_delete,R.id.iv_edit,R.id.iv_password,R.id.iv_upload)
    }

}

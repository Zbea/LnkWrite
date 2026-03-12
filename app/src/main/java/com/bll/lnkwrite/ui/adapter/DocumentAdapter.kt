package com.bll.lnkwrite.ui.adapter

import android.widget.ImageView
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.utils.fileManager.FileUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File

class DocumentAdapter(layoutResId: Int, data: List<File>?) : BaseQuickAdapter<File, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, file: File) {
        helper.setText(R.id.tv_name,file.name)
        val ivImage=helper.getView<ImageView>(R.id.iv_image)
        val format= FileUtils.getUrlFormat(file.path)
        when(format)
        {
            ".png",".jpg",".jpeg"->{
                MethodManager.setImageFile(file.path,ivImage)
                ivImage.setBackgroundResource(R.drawable.bg_gray_stroke_5dp_corner)
            }
            ".ppt",".pptx"->{
                ivImage.setImageResource(R.mipmap.icon_file_ppt)
                ivImage.setBackgroundResource(R.color.color_transparent)
            }
            else->{
                ivImage.setImageResource(R.mipmap.icon_file_document)
                ivImage.setBackgroundResource(R.color.color_transparent)
            }
        }
    }

}

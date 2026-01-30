package com.bll.lnkwrite.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.GlideUtils

class PreviewDialog(val context: Context, private val screenPos:Int,private val title:String,private val content:String, private val images:List<String>){

    private var page=0
    private val total=images.size-1
    private var tvPage:TextView?=null
    private var ivImage:ImageView?=null

    constructor(context: Context,title:String,content: String,images:List<String>) : this(context,2,title,content,images)

    fun builder(): PreviewDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_preview)
        val window=dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        val width=if (images.isNotEmpty()) DP2PX.dip2px(context,720F) else DP2PX.dip2px(context,500F)
        if (screenPos==1){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        }
        else{
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        layoutParams.width=width
        layoutParams.x=(Constants.WIDTH- width)/2
        dialog.show()

        ivImage=dialog.findViewById(R.id.iv_image)
        val ivClose=dialog.findViewById<ImageView>(R.id.iv_close)
        val rlImage=dialog.findViewById<RelativeLayout>(R.id.rl_image)
        val ivUp=dialog.findViewById<ImageView>(R.id.iv_up)
        val ivDown=dialog.findViewById<ImageView>(R.id.iv_down)
        tvPage=dialog.findViewById(R.id.tv_page)
        val tvContent=dialog.findViewById<TextView>(R.id.tv_content)
        val tvTitle=dialog.findViewById<TextView>(R.id.tv_title)

        tvTitle.text=title
        tvContent.text=content

        ivClose.setOnClickListener { dialog.dismiss() }

        if (images.isNotEmpty()){
            rlImage.visibility=View.VISIBLE
            setChange()
        }
        ivUp.setOnClickListener {
            if (page>0){
                page-=1
                setChange()
            }
        }

        ivDown.setOnClickListener {
            if (page<total){
                page+=1
                setChange()
            }
        }
        return this
    }

    private fun setChange(){
        GlideUtils.setImageUrl(context,images[page],ivImage)
        tvPage?.text="${page+1}/${total+1}"
    }

}
package com.bll.lnkwrite.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.widget.NumberPasswordView

class NumberPasswordDialog(val context: Context,private val screenPos:Int) {

    private var dialog: Dialog? = null
    private var tv_title:TextView?=null
    private var nv_view:NumberPasswordView?=null

    constructor(context: Context):this(context,0)

    fun builder(): NumberPasswordDialog {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_number_password)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =dialog?.window!!.attributes
        if (screenPos==1){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,550F))/2
        }
        else{
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,550F))/2
        }
        dialog?.show()

        val ivClose=dialog?.findViewById<ImageView>(R.id.iv_close)
        ivClose?.setOnClickListener { dialog?.dismiss() }
        tv_title = dialog?.findViewById(R.id.tv_title)
        nv_view=dialog?.findViewById(R.id.nv_view)
        nv_view?.onPwdComplete={ psd->
            onDialogClickListener?.onNumber(psd)
        }

        dialog?.setOnDismissListener {
            onDialogClickListener?.onDismiss()
        }

        return this
    }

    fun show() {
        dialog?.show()
    }

    fun cancel() {
        dialog?.dismiss()
    }

    fun reset(){
        nv_view?.reset()
    }

    fun setTitle(str:String){
        tv_title?.text=str
    }

    private var onDialogClickListener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onNumber(psw:String)
        fun onDismiss(){}
    }

    fun setDialogClickListener(onDialogClickListener: OnDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener
    }
}
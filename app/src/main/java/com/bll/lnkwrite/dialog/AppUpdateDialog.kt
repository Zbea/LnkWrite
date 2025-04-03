package com.bll.lnkwrite.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.AppUpdateBean


class AppUpdateDialog(private val context: Context,private val item:AppUpdateBean){

    private var dialog:Dialog?=null
    private var btn_ok:TextView?=null

    fun builder(): AppUpdateDialog {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_update)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog?.show()

        btn_ok = dialog?.findViewById(R.id.tv_update)
        val tv_name = dialog?.findViewById<TextView>(R.id.tv_title)
        val tv_info = dialog?.findViewById<TextView>(R.id.tv_info)
        tv_name?.text=item.versionName
        tv_info?.text=item.versionInfo

        return this
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    fun setUpdateBtn(string: String){
        if (btn_ok!=null){
            btn_ok?.text = string
        }
    }
}
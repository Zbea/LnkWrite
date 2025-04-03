package com.bll.lnkwrite.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.SystemUpdateInfo
import com.bll.lnkwrite.utils.AppUtils


class AppSystemUpdateDialog(private val context: Context, private val item:SystemUpdateInfo){


    fun builder(): AppSystemUpdateDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_update_system)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        val tv_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_update)
        btn_ok.setOnClickListener {
            dialog.dismiss()
            AppUtils.startAPP(context,Constants.PACKAGE_SYSTEM_UPDATE)
        }
        val tv_name = dialog.findViewById<TextView>(R.id.tv_title)
        val tv_info = dialog.findViewById<TextView>(R.id.tv_info)
        tv_name?.text="系统更新："+item.version
        tv_info?.text=item.description

        return this
    }

}
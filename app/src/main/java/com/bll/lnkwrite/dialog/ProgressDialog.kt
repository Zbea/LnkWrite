package com.bll.lnkwrite.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.utils.DP2PX

class ProgressDialog(var context: Context, val screenPos: Int) {
    var mDialog: Dialog? = null

    init {
        createDialog()
    }

    private fun createDialog() {
        mDialog = Dialog(context)
        mDialog!!.setContentView(R.layout.dialog_progress)
        val window = mDialog!!.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos == 1) {
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        } else {
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,120f))/2
        window.attributes = layoutParams
    }


    fun show() {
        mDialog?.show()
    }

    fun dismiss() {
        mDialog?.dismiss()
    }
}
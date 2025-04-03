package com.bll.lnkwrite.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.KeyboardUtils
import com.bll.lnkwrite.utils.MD5Utils
import com.bll.lnkwrite.utils.SToast


class PrivacyPasswordDialog(private val context: Context,private val type:Int=0) {

    fun builder(): PrivacyPasswordDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_check_password)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val etPassword=dialog.findViewById<EditText>(R.id.et_password)
        val tvFind = dialog.findViewById<TextView>(R.id.tv_find_password)

        tvFind.setOnClickListener {
            dialog.dismiss()
            PrivacyPasswordFindDialog(context,type).builder()
        }

        val tvEdit = dialog.findViewById<TextView>(R.id.tv_edit_password)
        tvEdit.setOnClickListener {
            dialog.dismiss()
            PrivacyPasswordEditDialog(context,type).builder()
        }

        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            if (passwordStr.isEmpty()){
                SToast.showText(R.string.password_input)
                return@setOnClickListener
            }
            val privacyPassword=MethodManager.getPrivacyPassword(type)
            if (MD5Utils.digest(passwordStr) != privacyPassword?.password){
                SToast.showText(R.string.password_error)
                return@setOnClickListener
            }
            listener?.onClick()
            dialog.dismiss()

        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick()
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}
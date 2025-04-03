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


class PrivacyPasswordEditDialog(private val context: Context,private val type:Int=0) {

    fun builder(): PrivacyPasswordEditDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_check_password_edit)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog.show()

        val privacyPassword=MethodManager.getPrivacyPassword(type)

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        val etPassword=dialog.findViewById<EditText>(R.id.et_password)
        val etPasswordAgain=dialog.findViewById<EditText>(R.id.et_password_again)
        val etPasswordOld=dialog.findViewById<EditText>(R.id.et_password_old)


        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            val passwordAgainStr=etPasswordAgain?.text.toString()
            val passwordOldStr=etPasswordOld?.text.toString()

            if (MD5Utils.digest(passwordOldStr)!=privacyPassword?.password){
                SToast.showText(R.string.password_old_error)
                return@setOnClickListener
            }
            if (passwordStr.isEmpty()||passwordAgainStr.isEmpty()){
                SToast.showText(R.string.password_input)
                return@setOnClickListener
            }
            if (passwordStr!=passwordAgainStr){
                SToast.showText(R.string.password_different)
                return@setOnClickListener
            }
            privacyPassword?.password= MD5Utils.digest(privacyPassword?.password)
            MethodManager.savePrivacyPassword(type, privacyPassword)
            dialog.dismiss()
            listener?.onClick()

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
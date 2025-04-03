package com.bll.lnkwrite.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.mvp.presenter.RegisterPresenter
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.utils.MD5Utils
import com.bll.lnkwrite.utils.SPUtil
import com.bll.lnkwrite.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_account_register.*


class AccountRegisterActivity : BaseActivity(), IContractView.IRegisterView {

    private val presenter= RegisterPresenter(this)
    private var countDownTimer: CountDownTimer? = null
    private var flags = 0

    override fun onSms() {
        showToast(R.string.send_verification_code_success)
        showCountDownView()
    }

    override fun onRegister() {
        setIntent()
    }
    override fun onFindPsd() {
        showToast(R.string.edit_success)
        setIntent()
    }


    override fun layoutId(): Int {
        return R.layout.ac_account_register
    }

    override fun initData() {
        flags=intent.flags
    }

    override fun initView() {
        when (flags) {
            1 -> {
                setPageTitle(R.string.password_edit)
                disMissView(ll_name)
                ed_user.setText(SPUtil.getString("account"))
                tv_password.setText(R.string.password_edit)
                btn_register.setText(R.string.commit)
            }
            else -> {
                setPageTitle(R.string.register)
            }
        }

        btn_code.setOnClickListener {
            val phone=ed_phone.text.toString().trim()
            if (!ToolUtils.isPhoneNum(phone)) {
                showToast(getString(R.string.phone_tip))
                return@setOnClickListener
            }
            presenter.sms(phone)
        }

        btn_register.setOnClickListener {

            val account=ed_user.text.toString().trim()
            val psd=ed_password.text.toString().trim()
            val name=ed_name.text.toString().trim()
            val phone=ed_phone.text.toString().trim()
            val code=ed_code.text.toString().trim()
            val role=3

            if (account.isEmpty()) {
                showToast(R.string.input_account_hint)
                return@setOnClickListener
            }
            if (psd.isEmpty()) {
                showToast(R.string.password_input)
                return@setOnClickListener
            }

            if (code.isEmpty()) {
                showToast(R.string.input_verification_code_hint)
                return@setOnClickListener
            }

            if (!ToolUtils.isLetterOrDigit(psd, 6, 20)) {
                showToast(getString(R.string.psw_tip))
                return@setOnClickListener
            }

            if (!ToolUtils.isPhoneNum(phone)) {
                showToast(getString(R.string.phone_tip))
                return@setOnClickListener
            }
            when (flags) {
                0 -> {
                    if (name.isEmpty()) {
                        showToast(R.string.input_name_hint)
                        return@setOnClickListener
                    }
                    if (!ToolUtils.isLetterOrDigit(account, 4, 12)) {
                        showToast(getString(R.string.user_tip))
                        return@setOnClickListener
                    }
                    val map=HashMap<String,Any>()
                    map["account"]=account
                    map["password"]=MD5Utils.digest(psd)
                    map["nickname"]=name
                    map["code"]=code
                    map["telNumber"]=phone
                    map["role"]=role
                    presenter.register(map)
                }
                1 -> {
                    presenter.findPsd(role.toString(),account,MD5Utils.digest(psd),phone, code)
                }
            }

        }

    }

    //验证码倒计时刷新ui
    private fun showCountDownView() {
        btn_code.isEnabled = false
        btn_code.isClickable = false
        countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onFinish() {
                runOnUiThread {
                    btn_code.isEnabled = true
                    btn_code.isClickable = true
                    btn_code.setText(R.string.get_verification_code_str)
                }

            }
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                runOnUiThread {
                    btn_code.text = "${millisUntilFinished / 1000}s"
                }
            }
        }.start()

    }

    private fun setIntent(){
        val intent = Intent()
        intent.putExtra("user", ed_user.text.toString())
        intent.putExtra("psw", ed_password.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}

package com.bll.lnkwrite.mvp.presenter

import android.util.Pair
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.*


class RegisterPresenter(view: IContractView.IRegisterView) : BasePresenter<IContractView.IRegisterView>(view) {

    fun register(map:HashMap<String,Any>) {

        val body = RequestUtils.getBody(map)

        val register = RetrofitManager.service.register(body)

        doRequest(register, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onRegister()
            }

        }, true)

    }

    fun findPsd(role: String,account: String, psd: String,phone: String,code: String) {


        val body = RequestUtils.getBody(

            Pair.create("account", account),
            Pair.create("password", psd),
            Pair.create("role", role),
            Pair.create("smsCode", code),
            Pair.create("telNumber", phone)

        )

        val findpsd = RetrofitManager.service.findPassword(body)

        doRequest(findpsd, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onFindPsd()
            }

        }, true)
    }


    fun sms(phone:String) {

        val sms = RetrofitManager.service.getSms(phone)

        doRequest(sms, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSms()
            }

        }, true)

    }


}
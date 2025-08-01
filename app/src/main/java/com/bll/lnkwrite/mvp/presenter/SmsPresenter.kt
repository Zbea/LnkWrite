package com.bll.lnkwrite.mvp.presenter

import android.util.Pair
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.BasePresenter
import com.bll.lnkwrite.net.BaseResult
import com.bll.lnkwrite.net.Callback
import com.bll.lnkwrite.net.RequestUtils
import com.bll.lnkwrite.net.RetrofitManager


class SmsPresenter(view: IContractView.ISmsView, val screen:Int=1) : BasePresenter<IContractView.ISmsView>(view) {

    fun sms(phone:String) {
        val sms = RetrofitManager.service.getSms(phone)
        doRequest(sms, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSms()
            }
        }, true)
    }

    fun checkPhone(code: String) {
        val body = RequestUtils.getBody(
            Pair.create("code", code)
        )
        val editName = RetrofitManager.service.checkPhone(body)
        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCheckSuccess()
            }
        }, true)
    }

}
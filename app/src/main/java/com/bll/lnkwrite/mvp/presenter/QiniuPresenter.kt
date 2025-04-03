package com.bll.lnkwrite.mvp.presenter

import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.BasePresenter
import com.bll.lnkwrite.net.BaseResult
import com.bll.lnkwrite.net.Callback
import com.bll.lnkwrite.net.RetrofitManager

class QiniuPresenter(view: IContractView.IQiniuView):
    BasePresenter<IContractView.IQiniuView>(view) {

    fun getToken(){
        val token = RetrofitManager.service.getQiniuToken()
        doRequest(token, object : Callback<String>(view) {
            override fun failed(tBaseResult: BaseResult<String>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<String>) {
                if (tBaseResult.data!=null)
                    view.onToken(tBaseResult.data)
            }
        }, true)
    }


}
package com.bll.lnkwrite.mvp.presenter

import com.bll.lnkwrite.mvp.model.CommonData
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.BasePresenter
import com.bll.lnkwrite.net.BaseResult
import com.bll.lnkwrite.net.Callback
import com.bll.lnkwrite.net.RetrofitManager


class CommonPresenter(view: IContractView.ICommonView,val screen:Int) : BasePresenter<IContractView.ICommonView>(view) {

    fun getCommon() {
        val editName = RetrofitManager.service.getCommonGrade()
        doRequest(editName, object : Callback<CommonData>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<CommonData>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CommonData>) {
                if (tBaseResult.data!=null)
                    view.onCommon(tBaseResult.data)
            }
        }, false)
    }
}
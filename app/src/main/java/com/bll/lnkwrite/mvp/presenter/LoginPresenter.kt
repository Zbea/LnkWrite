package com.bll.lnkwrite.mvp.presenter

import com.bll.lnkwrite.mvp.model.User
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.*


class LoginPresenter(view: IContractView.ILoginView,val screen:Int=1) : BasePresenter<IContractView.ILoginView>(view) {

    fun login(map: HashMap<String,Any>) {

        val body = RequestUtils.getBody(map)

        val login = RetrofitManager.service.login(body)
        doRequest(login, object : Callback<User>(view,screen) {
            override fun failed(tBaseResult: BaseResult<User>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<User>) {
                view.getLogin(tBaseResult.data)
            }

        }, true)

    }


    fun accounts() {
        val account = RetrofitManager.service.accounts()
        doRequest(account, object : Callback<User>(view,screen) {
            override fun failed(tBaseResult: BaseResult<User>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<User>) {
                view.getAccount(tBaseResult.data)
            }
        }, true)
    }


}
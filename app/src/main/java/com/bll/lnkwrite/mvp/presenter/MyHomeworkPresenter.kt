package com.bll.lnkwrite.mvp.presenter

import com.bll.lnkwrite.mvp.model.teaching.HomeworkTypeList
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.*


class MyHomeworkPresenter(view: IContractView.IMyHomeworkView,val screen:Int=0) : BasePresenter<IContractView.IMyHomeworkView>(view) {

    fun getHomeworks(map: HashMap<String,Any>) {
        val grade = RetrofitManager.service.getHomeworkTypes(map)
        doRequest(grade, object : Callback<HomeworkTypeList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<HomeworkTypeList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkTypeList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, false)
    }

    fun createHomeworkType(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val grade = RetrofitManager.service.createHomeworkType(body)
        doRequest(grade, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCreateSuccess()
            }
        }, true)
    }

    fun deleteHomeworkType(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val grade = RetrofitManager.service.deleteHomeworkType(body)
        doRequest(grade, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDelete()
            }
        }, true)
    }

    fun editHomeworkType(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val grade = RetrofitManager.service.editHomeworkType(body)
        doRequest(grade, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditSuccess()
            }
        }, true)
    }

    fun sendHomework(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val grade = RetrofitManager.service.sendHomework(body)
        doRequest(grade, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSendSuccess()
            }
        }, true)
    }

}
package com.bll.lnkwrite.mvp.presenter

import com.bll.lnkwrite.mvp.model.teaching.HomeworkCorrectList
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.*

class HomeworkCorrectPresenter(view: IContractView.IHomeworkCorrectView,val screen:Int=0):BasePresenter<IContractView.IHomeworkCorrectView>(view) {


    fun getCorrects(map: HashMap<String,Any>) {
        val grade = RetrofitManager.service.getHomeworkCorrects(map)
        doRequest(grade, object : Callback<HomeworkCorrectList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<HomeworkCorrectList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkCorrectList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, false)
    }

    fun deleteCorrect(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val grade = RetrofitManager.service.deleteCorrect(body)
        doRequest(grade, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, true)
    }

    fun commitPaperStudent(map:HashMap<String,Any>){
        val body= RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitPaperStudent(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onUpdateSuccess()
            }
        }, true)
    }

}
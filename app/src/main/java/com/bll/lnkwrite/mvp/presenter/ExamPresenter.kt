package com.bll.lnkwrite.mvp.presenter

import com.bll.lnkwrite.mvp.model.teaching.ExamList
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.*


class ExamPresenter(view: IContractView.IExamView,val screen:Int=0) : BasePresenter<IContractView.IExamView>(view) {

    fun getExams(map: HashMap<String,Any>) {
        val grade = RetrofitManager.service.getExams(map)
        doRequest(grade, object : Callback<ExamList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ExamList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ExamList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, false)
    }

    fun deleteExam(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val grade = RetrofitManager.service.deleteExams(body)
        doRequest(grade, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, true)
    }

}
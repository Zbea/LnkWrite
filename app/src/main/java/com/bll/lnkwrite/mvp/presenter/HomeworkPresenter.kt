package com.bll.lnkwrite.mvp.presenter

import com.bll.lnkwrite.mvp.model.TeacherHomeworkList
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.*


class HomeworkPresenter(view: IContractView.IHomeworkView,val screen:Int=0) : BasePresenter<IContractView.IHomeworkView>(view) {

    fun getHomeworks(map: HashMap<String,Any>) {
        val grade = RetrofitManager.service.getHomeworks(map)
        doRequest(grade, object : Callback<TeacherHomeworkList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TeacherHomeworkList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TeacherHomeworkList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, false)
    }

    fun deleteHomeworks(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val grade = RetrofitManager.service.deleteHomeworks(body)
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
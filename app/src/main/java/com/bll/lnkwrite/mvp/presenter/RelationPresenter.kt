package com.bll.lnkwrite.mvp.presenter

import com.bll.lnkwrite.mvp.model.StudentBean
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.*


class RelationPresenter(view: IContractView.IRelationView) : BasePresenter<IContractView.IRelationView>(view) {

    fun getStudents() {
        val grade = RetrofitManager.service.onStudentList()
        doRequest(grade, object : Callback<MutableList<StudentBean>>(view,2) {
            override fun failed(tBaseResult: BaseResult<MutableList<StudentBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<StudentBean>>) {
                if (tBaseResult.data!=null)
                    view.onListStudents(tBaseResult.data)
            }
        }, false)
    }
}
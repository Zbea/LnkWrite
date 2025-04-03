package com.bll.lnkwrite.mvp.presenter

import com.bll.lnkwrite.mvp.model.SchoolBean
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.net.BasePresenter
import com.bll.lnkwrite.net.BaseResult
import com.bll.lnkwrite.net.Callback
import com.bll.lnkwrite.net.RetrofitManager


class SchoolPresenter(view: IContractView.ISchoolView) : BasePresenter<IContractView.ISchoolView>(view) {

    fun getSchool() {
        val grade = RetrofitManager.service.getCommonSchool()
        doRequest(grade, object : Callback<MutableList<SchoolBean>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<SchoolBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<SchoolBean>>) {
                if (!tBaseResult.data.isNullOrEmpty())
                    view.onListSchools(tBaseResult.data)
            }
        }, true)
    }

}
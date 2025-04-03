package com.bll.lnkwrite.net


import com.bll.lnkwrite.R
import com.bll.lnkwrite.utils.SToast
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable

abstract class Callback<T> : Observer<BaseResult<T>> {

    private var IBaseView: IBaseView
    private var screen=0
    private var isComplete=true
    private var isShowToast=true

    constructor(IBaseView: IBaseView) {
        this.IBaseView = IBaseView
    }

    constructor(IBaseView: IBaseView,screen:Int) {
        this.IBaseView = IBaseView
        this.screen=screen
    }

    constructor(IBaseView: IBaseView,screen:Int,isShowToast: Boolean) {
        this.IBaseView = IBaseView
        this.screen=screen
        this.isShowToast=isShowToast
    }

    constructor(IBaseView: IBaseView,screen:Int,isShowToast:Boolean,isComplete: Boolean) {
        this.IBaseView = IBaseView
        this.screen=screen
        this.isShowToast=isShowToast
        this.isComplete=isComplete
    }

    override fun onSubscribe(@NonNull d: Disposable) {
        IBaseView.addSubscription(d)
    }

    override fun onNext(@NonNull tBaseResult: BaseResult<T>) {
        if (tBaseResult.code == 0) {
            success(tBaseResult)
        } else {
            when (tBaseResult.code) {
                -10 -> {
                    IBaseView.login()
                }
                else -> {
                    if (isShowToast)
                        IBaseView.fail(screen,tBaseResult.msg)
                    failed(tBaseResult)
                }
            }
        }
    }

    override fun onComplete() {
        if (isComplete)
            IBaseView.hideLoading()
    }

    override fun onError(@NonNull e: Throwable) {
        failed(BaseResult())
        e.printStackTrace()
        if (isShowToast){
            when (ExceptionHandle.handleException(e).code) {
                ExceptionHandle.ERROR.NETWORD_ERROR-> {
                    SToast.showText(screen, R.string.net_work_error)
                }
                ExceptionHandle.ERROR.SERVER_TIMEOUT_ERROR -> {
                    SToast.showText(screen,R.string.connect_server_timeout)
                }
                ExceptionHandle.ERROR.PARSE_ERROR -> {
                    SToast.showText(screen,R.string.data_parse_error)
                }
                ExceptionHandle.ERROR.HTTP_ERROR -> {
                    SToast.showText(screen,R.string.connect_server_error)
                }
                else -> {
                    SToast.showText(screen,R.string.connect_server_error)
                }
            }
        }
        IBaseView.hideLoading()
    }

    abstract fun failed(tBaseResult: BaseResult<T>): Boolean

    abstract fun success(tBaseResult: BaseResult<T>)
}
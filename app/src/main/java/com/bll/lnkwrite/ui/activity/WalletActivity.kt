package com.bll.lnkwrite.ui.activity

import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.WalletBuyDialog
import com.bll.lnkwrite.dialog.WalletStudentRechargeDialog
import com.bll.lnkwrite.mvp.model.AccountOrder
import com.bll.lnkwrite.mvp.model.AccountQdBean
import com.bll.lnkwrite.mvp.model.User
import com.bll.lnkwrite.mvp.presenter.WalletPresenter
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.utils.SPUtil
import com.king.zxing.util.CodeUtils
import kotlinx.android.synthetic.main.ac_wallet.*

class WalletActivity:BaseActivity(),IContractView.IWalletView{

    private var walletPresenter=WalletPresenter(this)
    private var xdDialog: WalletBuyDialog?=null
    private var xdList= mutableListOf<AccountQdBean>()
    private var qrCodeDialog:Dialog?=null
    private var orderThread: OrderThread?=null//定时器
    private val handlerThread = Handler(Looper.myLooper()!!)
    private var money=0

    override fun onXdList(list: MutableList<AccountQdBean>) {
        xdList= list
    }

    override fun onXdOrder(order: AccountOrder?) {
        showQrCodeDialog(order?.qrCode)
        checkOrderState(order?.outTradeNo)
    }

    override fun checkOrder(order: AccountOrder?) {
        //订单支付成功
        if (order?.status == 2) {
            handlerThread.removeCallbacks(orderThread!!)
            qrCodeDialog?.dismiss()
            runOnUiThread {
                mUser?.balance = mUser?.balance?.plus(order.amount)
                tv_xdmoney.text = "" + mUser?.balance
                SPUtil.putObj("user",mUser!!)
            }
        }
    }

    override fun transferSuccess() {
        tv_xdmoney.text=getString(R.string.xd)+":  "+(mUser?.balance!!-money)
        mUser?.balance=mUser?.balance!!-money
        SPUtil.putObj("user",mUser!!)
        showToast(R.string.transfer_success)
    }

    override fun getAccount(user: User) {
        mUser=user
        tv_xdmoney.text=getString(R.string.xd)+":  "+mUser?.balance
        SPUtil.putObj("user",mUser!!)
    }

    override fun layoutId(): Int {
        return R.layout.ac_wallet
    }

    override fun initData() {
        mUser= MethodManager.getUser()
    }

    override fun initView() {
        setPageTitle(R.string.my_wallet_str)

        tv_buy.setOnClickListener {
            if (xdList.size>0){
                getXdView()
            }
            else{
                walletPresenter.getXdList(true)
            }
        }

        tv_student.setOnClickListener {
            if (DataBeanManager.students.size==0){
                showToast(R.string.toast_bind_student)
                return@setOnClickListener
            }
            WalletStudentRechargeDialog(this,mUser?.balance!!).builder()?.setOnClickListener{
                money,id->
                this.money=money
                val map=HashMap<String,Any>()
                map["accountId"]=id
                map["balance"]=money
                walletPresenter.transferQd(map)
            }
        }

        walletPresenter.getXdList(false)
        walletPresenter.accounts()

    }

    //购买学豆
    private fun getXdView(){
        if (xdDialog==null){
            xdDialog= WalletBuyDialog(this,xdList).builder()
            xdDialog?.setOnDialogClickListener { id ->
                xdDialog?.dismiss()
                walletPresenter.postXdOrder(id)
            }
        }
        else{
            xdDialog?.show()
        }
    }

    //展示支付二维码的图片
    private fun showQrCodeDialog(url: String?) {
        qrCodeDialog = Dialog(this)
        qrCodeDialog?.setContentView(R.layout.dialog_account_qrcode)
        qrCodeDialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        qrCodeDialog?.setCanceledOnTouchOutside(false)
        val iv_qrcode = qrCodeDialog?.findViewById<ImageView>(R.id.iv_qrcode)
        qrCodeDialog?.show()
        val bitmap = CodeUtils.createQRCode(url, 300, null)
        iv_qrcode?.setImageBitmap(bitmap)

        val iv_close = qrCodeDialog?.findViewById<ImageView>(R.id.iv_close)
        iv_close?.setOnClickListener {
            qrCodeDialog?.dismiss()
            handlerThread.removeCallbacks(orderThread!!)
        }
    }

    //订单轮询 handler?
    private fun checkOrderState(orderID: String?) {
        //create thread
        if (orderThread != null) {
            handlerThread.removeCallbacks(orderThread!!)
        }
        orderThread = OrderThread(orderID)
        orderThread?.run()
    }

    //定时器 (定时请求订单状态)
    inner class OrderThread(private val orderID: String?) : Runnable {
        override fun run() {
            queryOrderById(orderID!!)
            handlerThread.postDelayed(this, 30*1000)
        }
        //查询订单状态接口
        private fun queryOrderById(orderID: String) {
            walletPresenter.checkOrder(orderID)
        }
    }


}
package com.bll.lnkwrite.ui.activity.account

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.*
import com.bll.lnkwrite.mvp.model.StudentBean
import com.bll.lnkwrite.mvp.presenter.AccountInfoPresenter
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.ui.adapter.AccountStudentAdapter
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.mvp.model.User
import com.bll.lnkwrite.mvp.presenter.SmsPresenter
import com.bll.lnkwrite.mvp.view.IContractView.ISmsView
import com.bll.lnkwrite.utils.MD5Utils
import com.bll.lnkwrite.utils.SPUtil
import com.bll.lnkwrite.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_account_info.*
import kotlinx.android.synthetic.main.ac_account_info.rv_list
import org.greenrobot.eventbus.EventBus

class AccountInfoActivity:BaseActivity(), IContractView.IAccountInfoView,ISmsView {

    private val smsPresenter= SmsPresenter(this)
    private val presenter=AccountInfoPresenter(this)
    private var nickname=""
    private var students= mutableListOf<StudentBean>()
    private var mAdapter: AccountStudentAdapter?=null
    private var position=0
    private var phone=""
    private var firstPsw=""
    private var currentPsw=""

    override fun onSms() {
        showToast(R.string.send_verification_code_success)
    }
    override fun onCheckSuccess() {
        editPhone()
    }

    override fun getAccount(user: User) {
        mUser=user
        SPUtil.putString(Constants.SP_PRIVACY_PASSWORD,user.privacyPassword)
        setAccountInfo()
    }

    override fun onPrivacyPassword() {
        SPUtil.putString(Constants.SP_PRIVACY_PASSWORD,if (currentPsw.isEmpty())"" else MD5Utils.digest(currentPsw))
        setPrivacyPswStr()
        showToast(if (currentPsw.isEmpty()) R.string.cancel_success else R.string.set_success)
    }

    override fun onEditPhone() {
        showToast(R.string.edit_success)
        mUser?.telNumber=phone
        tv_phone.text=getPhoneStr(phone)
    }

    override fun onEditNameSuccess() {
        showToast(R.string.edit_success)
        mUser?.nickname=nickname
        tv_name.text = nickname
    }
    override fun onBind() {
        presenter.getStudents()
    }
    override fun onUnbind() {
        mAdapter?.remove(position)
        DataBeanManager.students=students
        EventBus.getDefault().post(Constants.STUDENT_EVENT)
    }
    override fun onListStudent(bens: MutableList<StudentBean>) {
        students=bens
        mAdapter?.setNewData(students)
        if (DataBeanManager.students!=bens){
            DataBeanManager.students=bens
            EventBus.getDefault().post(Constants.STUDENT_EVENT)
        }
    }


    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        presenter.accounts()
        if (MethodManager.isCN())
            presenter.getStudents()
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setPageTitle(R.string.account)

        if (!MethodManager.isCN()){
            disMissView(ll_student,rv_list,btn_logout)
        }

        setAccountInfo()

        btn_edit_phone.setOnClickListener {
            EditPhoneDialog(this,mUser?.telNumber!!).builder().setOnDialogClickListener(object : EditPhoneDialog.OnDialogClickListener {
                override fun onClick(code: String, phone: String) {
                    this@AccountInfoActivity.phone=phone
                    smsPresenter.checkPhone(code)
                }
                override fun onPhone(phone: String) {
                    smsPresenter.sms(phone)
                }
            })
        }

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_add.setOnClickListener {
            add()
        }

        btn_edit_password.setOnClickListener {
            customStartActivity(Intent(this, AccountRegisterActivity::class.java).setFlags(1))
        }

        btn_privacy_password.setOnClickListener {
            editPrivacyPassword()
        }

        btn_logout.setOnClickListener {
            CommonDialog(this).setContent(R.string.tips_is_logout).builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    mUser=null
                    MethodManager.logout(this@AccountInfoActivity)
                }
            })
        }

        initRecyclerView()

    }

    private fun initRecyclerView(){
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = AccountStudentAdapter(R.layout.item_account_student,null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            when(view.id){
                R.id.tv_student_cancel->{
                    cancel()
                }
                R.id.tv_set->{
                    val intent = Intent(this, PermissionSettingActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable("studentInfo", students[position])
                    intent.putExtra("bundle", bundle)
                    customStartActivity(intent)
                }
            }
        }
    }

    private fun setAccountInfo(){
        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text =getPhoneStr(telNumber)
        }
        setPrivacyPswStr()
    }

    private fun setPrivacyPswStr(){
        val privacyPassword=SPUtil.getString(Constants.SP_PRIVACY_PASSWORD)
        btn_privacy_password.text=getString(if (privacyPassword.isEmpty()) R.string.password_set else R.string.password_cancel)
        tv_privacy_password.text=if (privacyPassword.isEmpty()) "" else "******"
    }

    private fun getPhoneStr(phone:String):String{
        return if (ToolUtils.isPhoneNum(phone)) phone.substring(0, 3) + "****" + phone.substring(7, 11) else ""
    }

    private fun editPrivacyPassword(){
        val privacyPassword=SPUtil.getString(Constants.SP_PRIVACY_PASSWORD)
        if (privacyPassword.isEmpty()){
            NumberPasswordDialog(this@AccountInfoActivity).builder().apply {
                setDialogClickListener(object : NumberPasswordDialog.OnDialogClickListener {
                    override fun onNumber(psw: String) {
                        if (firstPsw.isEmpty()){
                            firstPsw=psw
                            reset()
                            setTitle(getString(R.string.password_again))
                        }
                        else{
                            if (firstPsw==psw){
                                currentPsw=psw
                                cancel()
                                presenter.onPrivacyPassword(psw)
                            }
                            else{
                                reset()
                                showToast(R.string.password_different)
                            }
                        }
                    }
                    override fun onDismiss() {
                        firstPsw=""
                    }
                })
            }
        }
        else{
            NumberPasswordDialog(this@AccountInfoActivity).builder().apply {
                setDialogClickListener(object : NumberPasswordDialog.OnDialogClickListener {
                    override fun onNumber(psw: String) {
                        if (privacyPassword == MD5Utils.digest(psw)){
                            currentPsw=""
                            cancel()
                            presenter.onPrivacyPassword("-")
                        }
                        else{
                            reset()
                            showToast("密码输入错误")
                        }
                    }
                })
            }
        }
    }

    private fun editPhone(){
        EditPhoneDialog(this).builder().setOnDialogClickListener(object : EditPhoneDialog.OnDialogClickListener {
            override fun onClick(code: String, phone: String) {
                this@AccountInfoActivity.phone=phone
                presenter.editPhone(code, phone)
            }
            override fun onPhone(phone: String) {
                smsPresenter.sms(phone)
            }
        })
    }

    /**
     * 修改名称
     */
    private fun editName(){
        InputContentDialog(this,tv_name.text.toString()).builder()
            .setOnDialogClickListener { string ->
                nickname = string
                presenter.editName(nickname)
            }
    }

    /**
     * 关联
     */
    private fun add(){
        InputContentDialog(this,getString(R.string.input_account_hint)).builder()
            .setOnDialogClickListener { string ->
                presenter.onBindStudent(string)
            }
    }

    /**
     * 取消关联
     */
    private fun cancel(){
        CommonDialog(this).setContent(R.string.tips_is_unbind_student).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                presenter.unbindStudent(students[position].accountId)
            }
        })
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (Constants.REFRESH_STUDENT_PERMISSION_EVENT==msgFlag){
            presenter.getStudents()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mUser?.let { SPUtil.putObj("user", it) }
    }

    override fun onRefreshData() {
        presenter.getStudents()
    }

}
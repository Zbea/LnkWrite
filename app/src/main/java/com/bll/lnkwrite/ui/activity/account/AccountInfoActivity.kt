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
import com.bll.lnkwrite.mvp.presenter.SmsPresenter
import com.bll.lnkwrite.mvp.view.IContractView.ISmsView
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
    private var type=0

    override fun onSms() {
        showToast(R.string.send_verification_code_success)
        if (type==0){
            InputContentDialog(this,1,getString(R.string.input_verification_code_hint),1).builder().setOnDialogClickListener{
                smsPresenter.checkPhone(it)
            }
        }
    }
    override fun onCheckSuccess() {
        editPhone()
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
        mUser=MethodManager.getUser()
        if (MethodManager.isCN())
            presenter.getStudents()
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setPageTitle(R.string.account)

        if (!MethodManager.isCN()){
            disMissView(ll_student,rv_list,btn_logout)
        }

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text =getPhoneStr(telNumber)
        }

        btn_edit_phone.setOnClickListener {
            type=0
            smsPresenter.sms(mUser?.telNumber!!)
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

    private fun getPhoneStr(phone:String):String{
        return if (ToolUtils.isPhoneNum(phone)) phone.substring(0, 3) + "****" + phone.substring(7, 11) else ""
    }

    private fun editPhone(){
        EditPhoneDialog(this).builder().setOnDialogClickListener(object : EditPhoneDialog.OnDialogClickListener {
            override fun onClick(code: String, phone: String) {
                this@AccountInfoActivity.phone=phone
                presenter.editPhone(code, phone)
            }
            override fun onPhone(phone: String) {
                type=1
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
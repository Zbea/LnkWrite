package com.bll.lnkwrite.ui.fragment

import PopupClick
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.Constants.AUTO_REFRESH_EVENT
import com.bll.lnkwrite.Constants.DATE_DRAWING_EVENT
import com.bll.lnkwrite.Constants.MESSAGE_EVENT
import com.bll.lnkwrite.Constants.NOTE_EVENT
import com.bll.lnkwrite.Constants.STUDENT_EVENT
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.dialog.DiaryManageDialog
import com.bll.lnkwrite.dialog.DiaryUploadListDialog
import com.bll.lnkwrite.dialog.PrivacyPasswordCreateDialog
import com.bll.lnkwrite.dialog.PrivacyPasswordDialog
import com.bll.lnkwrite.manager.DiaryDaoManager
import com.bll.lnkwrite.manager.NoteDaoManager
import com.bll.lnkwrite.mvp.model.CloudListBean
import com.bll.lnkwrite.mvp.model.MessageList
import com.bll.lnkwrite.mvp.model.Note
import com.bll.lnkwrite.mvp.model.PopupBean
import com.bll.lnkwrite.mvp.model.StudentBean
import com.bll.lnkwrite.mvp.presenter.MessagePresenter
import com.bll.lnkwrite.mvp.presenter.RelationPresenter
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.ui.activity.DateActivity
import com.bll.lnkwrite.ui.activity.MessageListActivity
import com.bll.lnkwrite.ui.activity.drawing.DateEventActivity
import com.bll.lnkwrite.ui.activity.drawing.DiaryActivity
import com.bll.lnkwrite.ui.activity.drawing.FreeNoteActivity
import com.bll.lnkwrite.ui.adapter.MainNoteAdapter
import com.bll.lnkwrite.ui.adapter.MessageAdapter
import com.bll.lnkwrite.utils.DateUtils
import com.bll.lnkwrite.utils.FileUploadManager
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.utils.NetworkUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_right.iv_bg
import kotlinx.android.synthetic.main.fragment_main_right.iv_date
import kotlinx.android.synthetic.main.fragment_main_right.ll_message
import kotlinx.android.synthetic.main.fragment_main_right.ll_schedule
import kotlinx.android.synthetic.main.fragment_main_right.rv_main_message
import kotlinx.android.synthetic.main.fragment_main_right.rv_main_note
import kotlinx.android.synthetic.main.fragment_main_right.tv_diary_btn
import kotlinx.android.synthetic.main.fragment_main_right.tv_free_note
import org.greenrobot.eventbus.EventBus
import java.io.File


class MainRightFragment : BaseFragment(), IContractView.IRelationView,IContractView.IMessageView {
    private val presenter= RelationPresenter(this)
    private var mMessagePresenter= MessagePresenter(this,2)
    private var messages= mutableListOf<MessageList.MessageBean>()
    private var mMessageAdapter: MessageAdapter?=null

    private var notes= mutableListOf<Note>()
    private var mNoteAdapter: MainNoteAdapter?=null
    private var privacyPassword= MethodManager.getPrivacyPassword(0)
    private var diaryStartLong=0L
    private var diaryEndLong=0L
    private var diaryUploadTitleStr=""

    private var nowDay=0L

    override fun onListStudents(list: MutableList<StudentBean>) {
        if (list.size>0){
            showView(rv_main_message)
            findMessages()
        }
        else{
            disMissView(rv_main_message)
        }
        if (DataBeanManager.students != list){
            DataBeanManager.students=list
            EventBus.getDefault().post(STUDENT_EVENT)
        }
    }

    override fun onList(message: MessageList) {
        messages=message.list
        mMessageAdapter?.setNewData(messages)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_right
    }

    override fun initView() {
        MethodManager.setImageResource(requireActivity(), R.mipmap.icon_date_event_bg,iv_bg)

        tv_free_note.setOnClickListener {
            customStartActivity(Intent(requireActivity(), FreeNoteActivity::class.java))
        }

        tv_diary_btn.setOnClickListener {
            startDiaryActivity(0)
        }

        tv_diary_btn.setOnLongClickListener {
            onLongDiary()
            return@setOnLongClickListener true
        }

        ll_message.setOnClickListener {
            if (DataBeanManager.students.size>0)
                customStartActivity(Intent(requireActivity(), MessageListActivity::class.java))
        }

        ll_schedule.setOnClickListener {
            customStartActivity(Intent(activity, DateActivity::class.java))
        }

        iv_date.setOnClickListener {
            val intent = Intent(requireActivity(), DateEventActivity::class.java)
            intent.putExtra("date",nowDay)
            customStartActivity(intent)
        }

        setScheduleView()
        initMessageView()
        initNoteView()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected()) {
            presenter.getStudents()
        }
        findNotes()
    }

    private fun setScheduleView(){
        nowDay=DateUtils.getStartOfDayInMillis()
        val path= FileAddress().getPathDate(DateUtils.longToStringCalender(nowDay))+"/draw.png"
        if (File(path).exists()){
            val myBitmap= BitmapFactory.decodeFile(path)
            iv_date.setImageBitmap(myBitmap)
        }
        else{
            iv_date.setImageResource(0)
        }
    }

    private fun initNoteView(){
        rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mNoteAdapter=MainNoteAdapter(R.layout.item_main_note, null).apply {
            rv_main_note.adapter = this
            bindToRecyclerView(rv_main_note)
            setOnItemClickListener { adapter, view, position ->
                MethodManager.gotoNote(requireActivity(),notes[position])
            }
        }
    }


    //消息相关处理
    private fun initMessageView() {
        rv_main_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mMessageAdapter=MessageAdapter(R.layout.item_main_message, null).apply {
            rv_main_message.adapter = this
            bindToRecyclerView(rv_main_message)
        }
    }


    /**
     * 跳转日记
     */
    private fun startDiaryActivity(typeId:Int){
        if (privacyPassword!=null&&privacyPassword?.isSet==true){
            PrivacyPasswordDialog(requireActivity()).builder().setOnDialogClickListener{
                customStartActivity(Intent(activity,DiaryActivity::class.java).setFlags(typeId))
            }
        }
        else{
            customStartActivity(Intent(activity,DiaryActivity::class.java).setFlags(typeId))
        }
    }

    /**
     * 长按日记管理
     */
    private fun onLongDiary(){
        val pops= mutableListOf<PopupBean>()
        if (privacyPassword==null){
            pops.add(PopupBean(1,getString(R.string.password_set)))
        }
        else{
            if (privacyPassword?.isSet==true){
                pops.add(PopupBean(1,getString(R.string.password_cancel)))
            }
            else{
                pops.add(PopupBean(1,getString(R.string.password_set)))
            }
        }
        pops.add(PopupBean(2,getString(R.string.diary_save_str)))
        pops.add(PopupBean(3,getString(R.string.diary_cloud_str)))
        PopupClick(requireActivity(),pops,tv_diary_btn,0).builder().setOnSelectListener{
            when(it.id){
                1->{
                    if (privacyPassword==null){
                        PrivacyPasswordCreateDialog(requireActivity()).builder().setOnDialogClickListener{
                            privacyPassword=it
                            showToast(R.string.toast_password_set_success)
                        }
                    }
                    else{
                        val titleStr=if (privacyPassword?.isSet==true) getString(R.string.tips_is_password_set) else getString(R.string.tips_is_password_cancel)
                        CommonDialog(requireActivity()).setContent(titleStr).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                PrivacyPasswordDialog(requireActivity()).builder().setOnDialogClickListener{
                                    privacyPassword!!.isSet=!privacyPassword!!.isSet
                                    MethodManager.savePrivacyPassword(0,privacyPassword)
                                }
                            }
                        })
                    }
                }
                2->{
                    DiaryManageDialog(requireActivity(),1).builder().setOnDialogClickListener{
                            titleStr,startLong,endLong->
                        diaryStartLong=startLong
                        diaryEndLong=endLong
                        diaryUploadTitleStr=titleStr
                        val diarys= DiaryDaoManager.getInstance().queryList(diaryStartLong,diaryEndLong)
                        if (diarys.isNullOrEmpty()){
                            showToast(R.string.toast_content_null_no_upload)
                        }
                        else{
                            if (NetworkUtil.isNetworkConnected()){
                                mQiniuPresenter.getToken()
                            }
                            else{
                                showToast(R.string.net_work_error)
                            }
                        }
                    }
                }
                3->{
                    DiaryUploadListDialog(requireActivity()).builder().setOnDialogClickListener{ typeId->
                        startDiaryActivity(typeId)
                    }
                }
            }
        }
    }

    private fun findMessages(){
        val map=HashMap<String,Any>()
        map["page"]=1
        map["size"]=4
        mMessagePresenter.getList(map)
    }

    private fun findNotes(){
        notes = NoteDaoManager.getInstance().queryListOther(6)
        mNoteAdapter?.setNewData(notes)
    }

    override fun onRefreshData() {
        onCheckUpdate()
        lazyLoad()
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            AUTO_REFRESH_EVENT, DATE_DRAWING_EVENT ->{
                setScheduleView()
            }
            MESSAGE_EVENT -> {
                findMessages()
            }
            NOTE_EVENT->{
                findNotes()
            }
            STUDENT_EVENT->{
                if (NetworkUtil.isNetworkConnected()) {
                    presenter.getStudents()
                }
            }
        }
    }

    override fun onUpload(token: String) {
        cloudList.clear()
        val diarys= DiaryDaoManager.getInstance().queryList(diaryStartLong,diaryEndLong)
        val paths= mutableListOf<String>()
        for (item in diarys){
            paths.add(FileAddress().getPathDiary(DateUtils.longToStringCalender(item.date)))
        }
        val time=System.currentTimeMillis()
        FileUploadManager(token).apply {
            startUpload(paths,DateUtils.longToString(time))
            setCallBack{
                cloudList.add(CloudListBean().apply {
                    type=4
                    title=diaryUploadTitleStr
                    subTypeStr="我的日记"
                    year=DateUtils.getYear()
                    date=time
                    listJson= Gson().toJson(diarys)
                    downloadUrl=it
                })
                mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        val diarys=DiaryDaoManager.getInstance().queryList(diaryStartLong,diaryEndLong)
        for (item in diarys){
            val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(item.date))
            FileUtils.deleteFile(File(path))
            DiaryDaoManager.getInstance().delete(item)
        }
        showToast(R.string.upload_success)
    }

}
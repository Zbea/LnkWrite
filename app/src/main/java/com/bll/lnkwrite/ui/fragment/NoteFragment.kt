package com.bll.lnkwrite.ui.fragment

import PopupClick
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.*
import com.bll.lnkwrite.Constants.NOTE_TYPE_REFRESH_EVENT
import com.bll.lnkwrite.Constants.NOTE_EVENT
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.*
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.manager.NoteContentDaoManager
import com.bll.lnkwrite.manager.NoteDaoManager
import com.bll.lnkwrite.mvp.model.CloudListBean
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.mvp.model.Note
import com.bll.lnkwrite.mvp.model.PopupBean
import com.bll.lnkwrite.mvp.model.PrivacyPassword
import com.bll.lnkwrite.ui.activity.NotebookManagerActivity
import com.bll.lnkwrite.ui.adapter.NoteAdapter
import com.bll.lnkwrite.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_list_tab.*
import kotlinx.android.synthetic.main.common_title.*
import java.io.File

class NoteFragment:BaseFragment() {

    private var popupBeans = mutableListOf<PopupBean>()
    private var notes = mutableListOf<Note>()
    private var mAdapter: NoteAdapter? = null
    private var position = 0 //当前笔记标记
    private var tabPos = 0//当前笔记本标记
    private var typeStr=""
    private var privacyPassword:PrivacyPassword?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }
    override fun initView() {
        setTitle(R.string.note)
        showView(iv_manager)
        pageSize=14

        popupBeans.add(PopupBean(0, getString(R.string.notebook_manager)))
        popupBeans.add(PopupBean(1, getString(R.string.notebook_create)))

        privacyPassword=MethodManager.getPrivacyPassword(1)

        iv_manager?.setOnClickListener {
            PopupClick(requireActivity(), popupBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> customStartActivity(Intent(activity, NotebookManagerActivity::class.java))
                    1 -> createNoteBook()
                }
            }
        }

        initRecyclerView()
        initTabs()
    }

    override fun lazyLoad() {
    }

    /**
     * tab数据设置
     */
    private fun initTabs() {
        itemTabTypes.clear()
        pageIndex=1
        itemTabTypes=ItemTypeDaoManager.getInstance().queryAll(1)
        itemTabTypes.add(0,ItemTypeBean().apply {
            title = getString(R.string.note_tab_diary)
        })
        if (tabPos>=itemTabTypes.size){
            tabPos=0
        }
        itemTabTypes=MethodManager.setItemTypeBeanCheck(itemTabTypes,tabPos)
        typeStr = itemTabTypes[tabPos].title
        fetchData()
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        tabPos=position
        typeStr=itemTabTypes[position].title
        pageIndex=1
        fetchData()
    }

    private fun initRecyclerView() {

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(0, DP2PX.dip2px(requireActivity(),20f), 0,0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mAdapter = NoteAdapter(R.layout.item_note, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val note = notes[position]
            if (tabPos==0&&privacyPassword!=null&&!note.isCancelPassword){
                PrivacyPasswordDialog(requireActivity(),1).builder().setOnDialogClickListener{
                    MethodManager.gotoNote(requireActivity(),note)
                }
            }
            else{
                MethodManager.gotoNote(requireActivity(),note)
            }
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            val note=notes[position]
            when(view.id){
                R.id.iv_delete->{
                    CommonDialog(requireActivity(),2).setContent(R.string.tips_is_delete).builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                deleteNote()
                            }
                        })
                }
                R.id.iv_edit->{
                    InputContentDialog(requireContext(), 2,note.title).builder()
                        .setOnDialogClickListener { string ->
                            if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                                showToast(R.string.existed)
                                return@setOnDialogClickListener
                            }
                            //修改内容分类
                            NoteContentDaoManager.getInstance().editNoteTitles(note.typeStr,note.title,string)
                            note.title = string
                            NoteDaoManager.getInstance().insertOrReplace(note)
                            mAdapter?.notifyItemChanged(position)
                        }
                }
                R.id.iv_password->{
                    if (privacyPassword==null){
                        PrivacyPasswordCreateDialog(requireActivity(),1).builder().setOnDialogClickListener{
                            privacyPassword=it
                            mAdapter?.notifyDataSetChanged()
                            showToast(R.string.toast_password_set_success)
                        }
                    }
                    else{
                        val titleStr=if (note.isCancelPassword) getString(R.string.tips_is_password_set) else getString(R.string.tips_is_password_cancel)
                        CommonDialog(requireActivity(),2).setContent(titleStr).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                PrivacyPasswordDialog(requireActivity(),1).builder().setOnDialogClickListener{
                                    note.isCancelPassword=!note.isCancelPassword
                                    NoteDaoManager.getInstance().insertOrReplace(note)
                                    mAdapter?.notifyItemChanged(position)
                                }
                            }
                        })
                    }
                }
                R.id.iv_upload->{
                    val path=FileAddress().getPathNote(note.typeStr,note.title)
                    if (!FileUtils.isExistContent(path)){
                        showToast(note.title+getString(R.string.toast_content_null_no_upload))
                        return@setOnItemChildClickListener
                    }
                    CommonDialog(requireActivity(),2).setContent(R.string.tips_is_upload).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            mQiniuPresenter.getToken()
                        }
                    })
                }
            }
        }

        val view =requireActivity().layoutInflater.inflate(R.layout.common_add_view,null)
        view.setOnClickListener {
            ModuleItemDialog(requireContext(),2,getString(R.string.note),DataBeanManager.noteModules).builder()
                .setOnDialogClickListener { moduleBean ->
                    createNote(ToolUtils.getImageResStr(activity, moduleBean.resContentId))
                }
        }
        mAdapter?.addFooterView(view)
    }

    //新建笔记本
    private fun createNoteBook() {
        InputContentDialog(requireContext(),2,  getString(R.string.input_notebook_hint)).builder()
            .setOnDialogClickListener { string ->
                if (ItemTypeDaoManager.getInstance().isExist(string,1)){
                    showToast(R.string.existed)
                }
                else{
                    val noteBook = ItemTypeBean()
                    noteBook.type=1
                    noteBook.title = string
                    noteBook.date=System.currentTimeMillis()
                    ItemTypeDaoManager.getInstance().insertOrReplace(noteBook)
                    mTabTypeAdapter?.addData(noteBook)
                }
            }
    }

    //新建主题
    private fun createNote(resId:String) {
        val note = Note()
        InputContentDialog(requireContext(), 2, getString(R.string.input_note_hint)).builder()
            .setOnDialogClickListener { string ->
                if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(R.string.existed)
                    return@setOnDialogClickListener
                }
                note.title = string
                note.date = System.currentTimeMillis()
                note.typeStr = typeStr
                note.contentResId = resId

                NoteDaoManager.getInstance().insertOrReplace(note)
                if (notes.size==10){
                    pageIndex+=1
                    fetchData()
                }
                else{
                    mAdapter?.addData(0,note)
                }
            }
    }

    private fun deleteNote(){
        val note=notes[position]
        //删除主题
        NoteDaoManager.getInstance().deleteBean(note)
        //删除主题内容
        NoteContentDaoManager.getInstance().deleteType(note.typeStr, note.title)
        val path= FileAddress().getPathNote(note.typeStr,note.title)
        FileUtils.deleteFile(File(path))
        mAdapter?.remove(position)

        if (notes.size==0){
            if (pageIndex>1){
                pageIndex-=1
                fetchData()
            }
            else{
                setPageNumber(0)
            }
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            NOTE_TYPE_REFRESH_EVENT->{
                initTabs()
            }
            NOTE_EVENT->{
                fetchData()
            }
        }
    }

    override fun fetchData() {
        notes = NoteDaoManager.getInstance().queryAll(typeStr, pageIndex, pageSize)
        val total = NoteDaoManager.getInstance().queryAll(typeStr)
        setPageNumber(total.size)
        mAdapter?.setNewData(notes)
    }

    override fun onUpload(token: String) {
        cloudList.clear()
        val note=notes[position]
        val path=FileAddress().getPathNote(note.typeStr,note.title)
        //获取笔记所有内容
        val noteContents = NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title)
        FileUploadManager(token).apply {
            startUpload(path,note.title)
            setCallBack{
                cloudList.add(CloudListBean().apply {
                    type=3
                    title=note.title
                    subTypeStr=note.typeStr
                    date=System.currentTimeMillis()
                    listJson= Gson().toJson(note)
                    contentJson= Gson().toJson(noteContents)
                    downloadUrl=it
                })
                mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        deleteNote()
    }

}
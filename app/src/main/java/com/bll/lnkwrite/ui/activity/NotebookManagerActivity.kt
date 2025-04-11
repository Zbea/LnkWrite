package com.bll.lnkwrite.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.dialog.InputContentDialog
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.manager.NoteContentDaoManager
import com.bll.lnkwrite.manager.NoteDaoManager
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.ui.adapter.ItemTypeManagerAdapter
import com.bll.lnkwrite.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_page_number.ll_page_number
import org.greenrobot.eventbus.EventBus

class NotebookManagerActivity : BaseActivity() {

    private var noteBooks= mutableListOf<ItemTypeBean>()
    private var mAdapter: ItemTypeManagerAdapter? = null
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        noteBooks= ItemTypeDaoManager.getInstance().queryAll(1)
    }

    override fun initView() {
        setPageTitle(R.string.notebook_manager)
        disMissView(ll_page_number)
        initRecyclerView()
    }


    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,100f), DP2PX.dip2px(this,20f),DP2PX.dip2px(this,100f),DP2PX.dip2px(this,20f))
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ItemTypeManagerAdapter(R.layout.item_notebook_manager, noteBooks)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.iv_edit){
                editNoteBook(noteBooks[position].title)
            }
            if (view.id==R.id.iv_delete){
                deleteNotebook()
            }
            if (view.id==R.id.iv_top){
                val date=noteBooks[0].date//拿到最小时间
                noteBooks[position].date=date-1000
                ItemTypeDaoManager.getInstance().insertOrReplace(noteBooks[position])
                noteBooks.sortWith(Comparator { item1, item2 ->
                    return@Comparator item1.date.compareTo(item2.date)
                })
                setNotify()
            }
        }

    }

    //设置刷新通知
    private fun setNotify(){
        mAdapter?.notifyDataSetChanged()
        EventBus.getDefault().post(Constants.NOTE_TYPE_REFRESH_EVENT)
    }

    //删除
    private fun deleteNotebook(){
        CommonDialog(this).setContent(R.string.tips_is_delete).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    val noteType=noteBooks[position]
                    val notes= NoteDaoManager.getInstance().queryAll(noteType.title)
                    if (notes.isNotEmpty()){
                        showToast(R.string.toast_type_exist_no_delete)
                    }
                    else{
                        noteBooks.removeAt(position)
                        //删除笔记本
                        ItemTypeDaoManager.getInstance().deleteBean(noteType)
                        setNotify()
                    }
                }
            })
    }

    //修改笔记本
    private fun editNoteBook(content:String){
        InputContentDialog(this,content).builder().setOnDialogClickListener { string ->
            if (ItemTypeDaoManager.getInstance().isExist(string,1)){
                showToast(R.string.existed)
                return@setOnDialogClickListener
            }
            val noteBook=noteBooks[position]
            //修改笔记、内容分类
            val notes=NoteDaoManager.getInstance().queryAll(noteBook.title)
            for (note in notes){
                NoteContentDaoManager.getInstance().editNoteTypes(note.typeStr,note.title,string)
            }
            NoteDaoManager.getInstance().editNotes(noteBook.title,string)

            noteBook.title = string
            ItemTypeDaoManager.getInstance().insertOrReplace(noteBook)

            setNotify()
        }
    }

}
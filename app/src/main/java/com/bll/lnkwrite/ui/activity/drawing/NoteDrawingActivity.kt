package com.bll.lnkwrite.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseDrawingActivity
import com.bll.lnkwrite.dialog.CatalogDialog
import com.bll.lnkwrite.manager.NoteContentDaoManager
import com.bll.lnkwrite.manager.NoteDaoManager
import com.bll.lnkwrite.mvp.model.ItemList
import com.bll.lnkwrite.mvp.model.Note
import com.bll.lnkwrite.mvp.model.NoteContentBean
import com.bll.lnkwrite.utils.DateUtils
import com.bll.lnkwrite.utils.ToolUtils
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File

class NoteDrawingActivity : BaseDrawingActivity() {

    private var typeStr =""
    private var noteTitle=""
    private var noteBook: Note? = null
    private var note_Content_b: NoteContentBean? = null//当前内容
    private var note_Content_a: NoteContentBean? = null//a屏内容
    private var noteContents = mutableListOf<NoteContentBean>() //所有内容
    private var page = 0//页码

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val id = intent.getLongExtra("noteId",0)
        noteBook = NoteDaoManager.getInstance().queryBean(id)
        typeStr = noteBook?.typeStr.toString()
        noteTitle=noteBook?.title!!

        noteContents = NoteContentDaoManager.getInstance().queryAll(typeStr,noteTitle)

        if (noteContents.isNotEmpty()) {
            page = noteBook!!.page
            note_Content_b = if (page<noteContents.size){
                noteContents[page]
            } else{
                noteContents.last()
            }
        } else {
            newNoteContent()
        }
    }

    override fun initView() {
        disMissView(iv_btn)
        MethodManager.setImageResource(this,ToolUtils.getImageResId(this,noteBook?.contentResId),v_content_a)
        MethodManager.setImageResource(this,ToolUtils.getImageResId(this,noteBook?.contentResId),v_content_b)

        onContent()
    }

    override fun onCatalog() {
        var titleStr=""
        val list= mutableListOf<ItemList>()
        for (item in noteContents){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=noteContents.indexOf(item)
            itemList.isEdit=true
            if (titleStr != item.title)
            {
                titleStr=item.title
                list.add(itemList)
            }
        }
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list,true).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (page!=pageNumber){
                    page = pageNumber
                    onContent()
                }
            }
            override fun onEdit(title: String, pages: List<Int>) {
                for (page in pages){
                    val item=noteContents[page]
                    item.title=title
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(item)
                }
            }
        })
    }

    override fun onPageDown() {
        val total=noteContents.size-1
        if(isExpand){
            if (page<total-1){
                page+=2
                onContent()
            }
            else if (page==total-1){
                if (isDrawLastContent()){
                    newNoteContent()
                    onContent()
                }
                else{
                    page=total
                    onContent()
                }
            }
        }
        else{
            if (page ==total) {
                if (isDrawLastContent()){
                    newNoteContent()
                    onContent()
                }
            } else {
                page += 1
                onContent()
            }
        }
    }

    override fun onPageUp() {
        if(isExpand){
            if (page>2){
                page-=2
                onContent()
            }
            else if (page==2){
                page=1
                onContent()
            }
        }else{
            if (page>0){
                page-=1
                onContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        if (noteContents.size==1){
            //如果最后一张已写,则可以在全屏时创建新的
            if (isDrawLastContent()){
                newNoteContent()
            }
            else{
                return
            }
        }
        if (page==0){
            page=1
        }
        isExpand=!isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    /**
     * 最后一个是否已写
     */
    private fun isDrawLastContent():Boolean{
        val contentBean = noteContents.last()
        return File(contentBean.filePath).exists()
    }

    override fun onContent() {
        note_Content_b = noteContents[page]
        if (isExpand)
            note_Content_a = noteContents[page-1]

        tv_page_total.text="${noteContents.size}"
        tv_page_total_a.text="${noteContents.size}"

        setElikLoadPath(elik_b!!, note_Content_b!!.filePath)
        tv_page.text = "${page+1}"
        if (isExpand) {
            setElikLoadPath(elik_a!!, note_Content_a!!.filePath)
            if (screenPos== Constants.SCREEN_RIGHT){
                tv_page_a.text="$page"
            }
            else{
                tv_page.text="$page"
                tv_page_a.text="${page+1}"
            }
        }
    }

    //保存绘图以及更新手绘
    private fun setElikLoadPath(elik: EinkPWInterface, path: String) {
        elik.setLoadFilePath(path, true)
    }


    //创建新的作业内容
    private fun newNoteContent() {
        val date=System.currentTimeMillis()
        val path=FileAddress().getPathNote(typeStr,noteTitle)

        note_Content_b = NoteContentBean()
        note_Content_b?.date=date
        note_Content_b?.typeStr=typeStr
        note_Content_b?.notebookTitle = noteTitle
        note_Content_b?.resId = noteBook?.contentResId
        note_Content_b?.title=getString(R.string.unnamed)+(noteContents.size+1)
        note_Content_b?.filePath = "$path/${DateUtils.longToString(date)}.png"
        page = noteContents.size

        noteContents.add(note_Content_b!!)
        NoteContentDaoManager.getInstance().insertOrReplaceNote(note_Content_b)
    }


    override fun onPause() {
        super.onPause()
        noteBook?.page=page
        NoteDaoManager.getInstance().insertOrReplace(noteBook)
    }

}
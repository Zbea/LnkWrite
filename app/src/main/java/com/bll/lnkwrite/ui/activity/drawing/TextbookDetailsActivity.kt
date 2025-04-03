package com.bll.lnkwrite.ui.activity.drawing

import android.view.EinkPWInterface
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.Constants.TEXT_BOOK_EVENT
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseDrawingActivity
import com.bll.lnkwrite.dialog.CatalogBookDialog
import com.bll.lnkwrite.manager.TextbookGreenDaoManager
import com.bll.lnkwrite.mvp.model.book.TextbookBean
import com.bll.lnkwrite.mvp.model.catalog.CatalogChildBean
import com.bll.lnkwrite.mvp.model.catalog.CatalogMsg
import com.bll.lnkwrite.mvp.model.catalog.CatalogParentBean
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.utils.GlideUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.*
import org.greenrobot.eventbus.EventBus
import java.io.File


class TextbookDetailsActivity : BaseDrawingActivity(){

    private var book: TextbookBean? = null
    private var catalogMsg: CatalogMsg? = null
    private var catalogs = mutableListOf<MultiItemEntity>()
    private var startCount=0
    private var page = 0 //当前页码

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val id=intent.getIntExtra("book_id",0)
        val type=intent.getIntExtra("book_type",0)
        book = TextbookGreenDaoManager.getInstance().queryTextBookByBookId(type,id)
        if (book == null) return
        page = book?.pageIndex!!
        val catalogFilePath = FileAddress().getPathTextBookCatalog(book?.bookPath!!)
        if (FileUtils.isExist(catalogFilePath))
        {
            val catalogMsgStr = FileUtils.readFileContent(FileUtils.file2InputStream(File(catalogFilePath)))
            catalogMsg = Gson().fromJson(catalogMsgStr, CatalogMsg::class.java)
            if (catalogMsg!=null){
                for (item in catalogMsg?.contents!!) {
                    val catalogParentBean = CatalogParentBean()
                    catalogParentBean.title = item.title
                    catalogParentBean.pageNumber = item.pageNumber
                    catalogParentBean.picName = item.picName
                    for (ite in item.subItems) {
                        val catalogChildBean = CatalogChildBean()
                        catalogChildBean.title = ite.title
                        catalogChildBean.pageNumber = ite.pageNumber
                        catalogChildBean.picName = ite.picName
                        catalogParentBean.addSubItem(catalogChildBean)
                    }
                    catalogs.add(catalogParentBean)
                }
                pageCount =  catalogMsg?.totalCount!!
                startCount =  if (catalogMsg?.startCount!!-1<0)0 else catalogMsg?.startCount!!-1
            }
        }
        else{
            pageCount=FileUtils.getFiles(FileAddress().getPathTextBookPicture(book?.bookPath!!)).size
        }
    }

    override fun initView() {
        disMissView(iv_btn)

        onContent()
    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 1) {
                page -= 2
                onContent()
            } else {
                page = 1
                onContent()
            }
        } else {
            if (page > 0) {
                page -= 1
                onContent()
            }
        }
    }

    override fun onPageDown() {
        if (isExpand){
            if (page<pageCount-2){
                page+=2
                onContent()
            }
            else if (page==pageCount-2){
                page=pageCount-1
                onContent()
            }
        }
        else{
            if (page<pageCount-1){
                page+=1
                onContent()
            }
        }
    }

    override fun onCatalog() {
        CatalogBookDialog(this,screenPos, getCurrentScreenPos(),catalogs, startCount).builder().setOnDialogClickListener { pageNumber ->
            if (page != pageNumber - 1) {
                page = pageNumber - 1
                onContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    /**
     * 更新内容
     */
    override fun onContent() {
        if (pageCount==0)
            return
        if (page>=pageCount){
            page=pageCount-1
            return
        }
        if (page==0&&isExpand){
            page=1
        }

        loadPicture(page, elik_b!!, v_content_b!!)
        setPageCurrent(page,tv_page,tv_page_total)
        if (isExpand){
            val page_up=page-1//上一页页码
            loadPicture(page_up, elik_a!!, v_content_a!!)
            if (screenPos== Constants.SCREEN_RIGHT){
                setPageCurrent(page_up,tv_page_a,tv_page_total_a)
            }
            else{
                setPageCurrent(page_up,tv_page,tv_page_total)
                setPageCurrent(page,tv_page_a,tv_page_total_a)
            }
        }
    }

    /**
     * 设置当前页面页码
     */
    private fun setPageCurrent(currentPage:Int, tvPage: TextView, tvPageTotal: TextView){
        tvPage.text = if (currentPage>=startCount) "${currentPage-startCount+1}" else ""
        tvPageTotal.text=if (currentPage>=startCount) "${pageCount-startCount}" else ""
    }

    //加载图片
    private fun loadPicture(index: Int, elik: EinkPWInterface, view: ImageView) {
        val showFile = FileUtils.getIndexFile(book?.bookPath,index)
        if (showFile != null) {
            GlideUtils.setImageCacheUrl(this, showFile.path, view)
            val drawPath = book?.bookDrawPath+"/${index+1}.png"
            elik.setLoadFilePath(drawPath, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.pageIndex = page
        book?.pageUrl = FileUtils.getIndexFile(book?.bookPath,page).path
        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
        EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }

}
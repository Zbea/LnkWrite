package com.bll.lnkwrite.ui.activity.drawing

import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseDrawingActivity
import com.bll.lnkwrite.dialog.CatalogDialog
import com.bll.lnkwrite.dialog.PaintingLinerSelectDialog
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.manager.PaintingContentDaoManager
import com.bll.lnkwrite.mvp.model.ItemList
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.mvp.model.PaintingContentBean
import com.bll.lnkwrite.utils.DateUtils
import com.bll.lnkwrite.utils.SPUtil
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class PaintingDrawingActivity : BaseDrawingActivity() {

    private var paintingTypeBean:ItemTypeBean?=null
    private var typeStr =""
    private var item_b: PaintingContentBean? = null//当前内容
    private var paintingContentBean_a: PaintingContentBean? = null//a屏内容
    private var items = mutableListOf<PaintingContentBean>() //所有内容
    private var page = 0//页码
    private var linerDialog: PaintingLinerSelectDialog?=null

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        typeStr= intent.getStringExtra("paintingType").toString()
        paintingTypeBean=ItemTypeDaoManager.getInstance().queryBean(5,typeStr)
        page = paintingTypeBean!!.page
        items = PaintingContentDaoManager.getInstance().queryAll(typeStr)

        if (items.isNotEmpty()) {
            item_b = if (page<items.size){
                items[page]
            } else{
                items.last()
            }
        } else {
            newNoteContent()
        }
    }

    override fun initView() {
        //设置初始笔形
        val drawType= SPUtil.getInt(Constants.SP_PAINTING_DRAW_TYPE)
        setDrawOjectType(if (drawType==0) PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN else drawType)

        iv_btn.setImageResource(R.mipmap.icon_draw_setting)
        iv_btn.setOnClickListener {
            if (linerDialog==null){
                linerDialog=PaintingLinerSelectDialog(this).builder()
                linerDialog!!.setOnSelectListener(object : PaintingLinerSelectDialog.OnSelectListener {
                    override fun setWidth(width: Int) {
                        setDrawWidth(width)
                    }
                    override fun setDrawType(drawType: Int) {
                        setDrawOjectType(drawType)
                        SPUtil.putInt(Constants.SP_PAINTING_DRAW_TYPE,drawType)
                    }
                    override fun setOpenRule(isOPen: Boolean) {
                        if (isOPen){
                            setBg(R.mipmap.icon_painting_draw_hb)
                        }
                        else{
                            setBg(0)
                        }
                    }
                })
            }
            else{
                linerDialog?.show()
            }
        }

        onContent()
    }

    override fun onCatalog() {
        var titleStr=""
        val list= mutableListOf<ItemList>()
        for (item in items){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=items.indexOf(item)
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
                    val item=items[page]
                    item.title=title
                    PaintingContentDaoManager.getInstance().insertOrReplace(item)
                }
            }
        })
    }

    override fun onPageDown() {
        val total=items.size-1
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
        if (items.size==1){
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
        val contentBean = items.last()
        return File(contentBean.path).exists()
    }

    override fun onContent() {
        item_b = items[page]
        if (isExpand)
            paintingContentBean_a = items[page-1]

        tv_page_total.text="${items.size}"
        tv_page_total_a.text="${items.size}"

        setElikLoadPath(elik_b!!, item_b!!.path)
        tv_page.text = "${page+1}"
        if (isExpand) {
            setElikLoadPath(elik_a!!, paintingContentBean_a!!.path)
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

    private fun setBg(resId:Int){
        MethodManager.setImageResource(this,resId,v_content_a)
        MethodManager.setImageResource(this,resId,v_content_b)
    }

    //创建新的作业内容
    private fun newNoteContent() {
        val date=System.currentTimeMillis()
        val path=FileAddress().getPathPainting(typeStr)

        item_b = PaintingContentBean()
        item_b?.date=date
        item_b?.typeStr=typeStr
        item_b?.title=getString(R.string.unnamed)+(items.size+1)
        item_b?.path = "$path/${DateUtils.longToString(date)}.png"
        page = items.size

        items.add(item_b!!)
    }


    override fun onPause() {
        super.onPause()
        paintingTypeBean?.page=page
        ItemTypeDaoManager.getInstance().insertOrReplace(paintingTypeBean)
    }

}
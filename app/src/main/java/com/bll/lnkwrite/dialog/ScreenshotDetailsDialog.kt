package com.bll.lnkwrite.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.mvp.model.ItemDetailsBean
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.widget.FlowLayoutManager
import com.bll.lnkwrite.widget.MaxRecyclerView
import com.bll.lnkwrite.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File

class ScreenshotDetailsDialog(val context: Context) {

    fun builder(): ScreenshotDetailsDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_bookcase_list)
        val window= dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,600F))/2
        dialog.show()

        var total=0
        val items= mutableListOf<ItemDetailsBean>()

        val screenTypes= ItemTypeDaoManager.getInstance().queryAll(3)
        screenTypes.add(0,MethodManager.getDefaultItemTypeScreenshot())

        for (item in screenTypes){
            val files= FileUtils.getDescFiles(item.path)
            if (files.isNotEmpty()){
                items.add(ItemDetailsBean().apply {
                    typeStr=item.title
                    num=files.size
                    this.files=files
                })
                total+=files.size
            }
        }

        val tv_title=dialog.findViewById<TextView>(R.id.tv_title)
        tv_title.setText(R.string.screenshot_details_str)

        val tv_total=dialog.findViewById<TextView>(R.id.tv_total)
        tv_total.text=context.getString(R.string.total)+"：${total}"

        val rv_list=dialog.findViewById<MaxRecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = ScreenshotDetailsAdapter(R.layout.item_bookcase_list, items)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceItemDeco(30))
        mAdapter.setOnChildClickListener{ parentPos,pos->
            dialog.dismiss()
            val path=screenTypes[parentPos].path
            val files=FileUtils.getDescFiles(path)
            MethodManager.gotoScreenFile(context,files.size-pos-1,screenTypes[parentPos].path)
        }

        return this
    }


    class ScreenshotDetailsAdapter(layoutResId: Int, data: List<ItemDetailsBean>?) : BaseQuickAdapter<ItemDetailsBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ItemDetailsBean) {
            helper.setText(R.id.tv_book_type,item.typeStr)
            helper.setText(R.id.tv_book_num,"(${item.num})")

            val recyclerView = helper.getView<RecyclerView>(R.id.rv_list)
            recyclerView?.layoutManager = FlowLayoutManager()
            val mAdapter = ChildAdapter(R.layout.item_bookcase_name,item.files)
            recyclerView?.adapter = mAdapter
            mAdapter.setOnItemClickListener { adapter, view, position ->
                listener?.onClick(helper.adapterPosition,position)
            }
        }

        class ChildAdapter(layoutResId: Int,  data: List<File>?) : BaseQuickAdapter<File, BaseViewHolder>(layoutResId, data) {
            override fun convert(helper: BaseViewHolder, item: File) {
                helper.apply {
                    helper.setText(R.id.tv_name, FileUtils.getFileName(item.name))
                }
            }
        }

        private var listener: OnChildClickListener? = null

        fun interface OnChildClickListener {
            fun onClick(parentPos:Int,pos: Int)
        }
        fun setOnChildClickListener(listener: OnChildClickListener?) {
            this.listener = listener
        }
    }

}
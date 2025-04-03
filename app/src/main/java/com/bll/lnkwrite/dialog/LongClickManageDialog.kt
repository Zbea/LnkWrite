package com.bll.lnkwrite.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity.CENTER_VERTICAL
import android.view.Gravity.END
import android.view.Gravity.START
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.ItemList
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.widget.SpaceGridItemDecoScore
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class LongClickManageDialog(val context: Context, val screenPos:Int, val name:String, val lists:MutableList<ItemList>){

    constructor(context: Context,name: String, lists:MutableList<ItemList>):this(context,1,name,lists)

    fun builder(): LongClickManageDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_item_select)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = dialog.window?.attributes
        if (screenPos==1){
            layoutParams?.gravity = CENTER_VERTICAL or START
        }
        else{
            layoutParams?.gravity = CENTER_VERTICAL or END
        }
        layoutParams?.x=(Constants.WIDTH- DP2PX.dip2px(context,450F))/2
        dialog.show()

        val tv_name=dialog.findViewById<TextView>(R.id.tv_name)
        val iv_close=dialog.findViewById<ImageView>(R.id.iv_close)
        tv_name.text=name
        iv_close.setOnClickListener {
            dialog.dismiss()
        }

        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = GridLayoutManager(context,2)
        val mAdapter = MyAdapter(R.layout.item_long_click, lists)
        rv_list?.adapter = mAdapter
        rv_list?.addItemDecoration(SpaceGridItemDecoScore(20, 30))
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            onClickListener?.onClick(position)
            dialog.dismiss()
        }

        return this
    }

    private var onClickListener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(position:Int)
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }

    class MyAdapter(layoutResId: Int, data: List<ItemList>?) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ItemList) {
            helper.setText(R.id.tv_name,item.name)
            helper.setImageResource(R.id.iv_image,item.resId)
        }
    }

}
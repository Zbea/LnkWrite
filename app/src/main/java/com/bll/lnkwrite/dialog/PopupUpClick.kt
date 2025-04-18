package com.bll.lnkwrite.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.PopupBean
import com.bll.lnkwrite.widget.MaxRecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PopupUpClick(var context:Context, var list:MutableList<PopupBean>, var view: View, val width:Int, var xoff:Int, var yoff:Int) {

    private var mPopupWindow:PopupWindow?=null
    private var height=0

    fun builder(): PopupUpClick{
        val popView = LayoutInflater.from(context).inflate(R.layout.popup_list_up, null, false)
        mPopupWindow = PopupWindow(context).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置PopupWindow的内容view
            contentView=popView
            isFocusable=true // 设置PopupWindow可获得焦点
            isTouchable=true // 设置PopupWindow可触摸
            isOutsideTouchable=true // 设置非PopupWindow区域可触摸
            isClippingEnabled = false
            if (this@PopupUpClick.width!=0){
                width=this@PopupUpClick.width
            }
        }

        val rvList=popView.findViewById<MaxRecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        val mAdapter = MAdapter(R.layout.item_popwindow_list, list)
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (onSelectListener!=null)
                onSelectListener?.onSelect(list[position])
            dismiss()
        }

        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        height=mPopupWindow?.contentView?.measuredWidth!!

        show()
        return this
    }

    fun dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow?.dismiss()
        }
    }

    fun show() {
        if (mPopupWindow != null) {
            mPopupWindow?.showAsDropDown(view,xoff, -height+yoff,Gravity.START)
        }
    }

   private var onSelectListener:OnSelectListener?=null

    fun setOnSelectListener(onSelectListener:OnSelectListener)
    {
        this.onSelectListener=onSelectListener
    }

    fun interface OnSelectListener{
        fun onSelect(item: PopupBean)
    }

    private class MAdapter(layoutResId: Int, data: List<PopupBean>?) : BaseQuickAdapter<PopupBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: PopupBean) {
            helper.setText(R.id.tv_name,item.name)
            helper.setImageResource(R.id.iv_check,item.resId)
            helper.setGone(R.id.iv_check, item.resId!=0)
        }

    }

}
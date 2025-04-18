package com.bll.lnkwrite.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.bll.lnkwrite.R
import com.bll.lnkwrite.utils.DateUtils
import com.bll.lnkwrite.widget.WheelView

/**
 * 时间选择器
 */
class PopupDateSelector(var context:Context, var view: View, val nums:List<Int>, val type: Int) {

    private var mPopupWindow:PopupWindow?=null

    fun builder(): PopupDateSelector?{
        val popView = LayoutInflater.from(context).inflate(R.layout.popup_date_number_selector, null, false)
        mPopupWindow = PopupWindow(context).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置PopupWindow的内容view
            contentView=popView
            isFocusable=true // 设置PopupWindow可获得焦点
            isTouchable=true // 设置PopupWindow可触摸
            isOutsideTouchable=true // 设置非PopupWindow区域可触摸
            width=view.width
        }

        var pos=0
        if (type==0){
            for (i in nums.indices)
            {
                if (nums[i]==DateUtils.getYear())
                    pos=i
            }
        }
        else{
            for (i in nums.indices)
            {
                if (nums[i]==DateUtils.getMonth())
                    pos=i
            }
        }


        val wv_view = popView.findViewById<WheelView>(R.id.wv_view)
        wv_view.setOffset(2)
        wv_view.setItems(nums)
        wv_view.setSelection(pos)
        wv_view.setOnWheelViewListener(object : WheelView.OnWheelViewListener {
            override fun onSelector(selectedIndex: Int, item: String?) {
                onSelectorListener?.onSelect(item!!)
            }

            override fun onClick(item: String?) {
                onSelectorListener?.onSelect(item!!)
                dismiss()
            }

        })


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
            mPopupWindow?.showAsDropDown(view,0, 5)
        }
    }

   private var onSelectorListener:OnSelectorListener?=null

    fun setOnSelectorListener(onSelectorListener:OnSelectorListener)
    {
        this.onSelectorListener=onSelectorListener
    }

    fun interface OnSelectorListener{
        fun onSelect(date: String)
    }



}
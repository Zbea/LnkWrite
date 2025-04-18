package com.bll.lnkwrite.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil

class SpaceGridItemDeco(private val count: Int, private val height: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)+1//当前位置
        val total=state.itemCount
        val line = ceil(total.toDouble() / count).toInt()//最后一行
        val currentLine=ceil(position.toDouble() / count).toInt()//当前行
        if (currentLine!=line){
            outRect.bottom = height
        }
    }

}
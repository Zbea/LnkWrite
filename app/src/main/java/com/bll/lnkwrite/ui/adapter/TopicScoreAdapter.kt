package com.bll.lnkwrite.ui.adapter

import android.widget.TextView
import com.bll.lnkwrite.mvp.model.teaching.ExamScoreItem
import com.bll.lnkwrite.utils.ToolUtils
import com.bll.lnkwrite.R
import com.bll.lnkwrite.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicScoreAdapter(layoutResId: Int, private var scoreType:Int, private var module:Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
        helper.setText(R.id.tv_sort,if (module==1) ToolUtils.numbers[item.sort+1] else "${item.sort+1}")
        helper.getView<TextView>(R.id.tv_sort).layoutParams.width=if (module==1) DP2PX.dip2px(mContext,55f) else DP2PX.dip2px(mContext,40f)
        helper.setText(R.id.tv_score,if (scoreType==1)item.score.toString() else if (item.result==1)"对" else "错" )
        helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
        helper.addOnClickListener(R.id.tv_score,R.id.iv_result)
    }
}

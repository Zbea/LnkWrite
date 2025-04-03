package com.bll.lnkwrite.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.mvp.model.ModuleBean
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class ModuleItemDialog(private val context: Context, private val screenPos:Int, val title:String, val list:MutableList<ModuleBean>) {

    private var dialog:Dialog?=null

    fun builder(): ModuleItemDialog {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_module_select)
        val window = dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val width=if (list.size>4) DP2PX.dip2px(context,700f) else DP2PX.dip2px(context,500f)
        val layoutParams = window.attributes
        layoutParams.width=width
        layoutParams.x=(Constants.WIDTH- width)/2
        if (screenPos==1){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        }
        else{
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        dialog?.show()


        val tvName = dialog?.findViewById<TextView>(R.id.tv_name)
        tvName?.text=title

        val iv_cancel = dialog?.findViewById<ImageView>(R.id.iv_close)
        iv_cancel?.setOnClickListener { dialog?.dismiss() }

        val count=if (list.size>4) 3 else 2
        val rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        rvList?.layoutManager = GridLayoutManager(context,count)
        val mAdapter =MAdapter(R.layout.item_module, list)
        rvList?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco(count,40))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (listener!=null)
                listener?.onClick(list[position])
            dismiss()
        }

        return this
    }


    fun dismiss(){
        if(dialog!=null)
            dialog?.dismiss()
    }

    fun show(){
        if(dialog!=null)
            dialog?.show()
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(item: ModuleBean)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    private class MAdapter(layoutResId: Int, data: List<ModuleBean>?) : BaseQuickAdapter<ModuleBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ModuleBean) {

            helper.setText(R.id.tv_name,item.name)

            helper.getView<ImageView>(R.id.iv_image).setImageResource(item.resId)

        }

    }

}
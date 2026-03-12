package com.bll.lnkwrite.base

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R


abstract class BaseDialog(protected val context: Context,private val screenPos:Int=0,private val targetOffsetDp: Float = 0f) {
    protected var dialog: Dialog? = null
    // 默认弹窗宽度
    protected open val defaultWidth: Int = WindowManager.LayoutParams.WRAP_CONTENT
    // 默认弹窗位置（居中）
    // 动态计算弹窗位置（基于screenPos）
    protected open val defaultGravity: Int
        get() = when(screenPos){
            0->Gravity.CENTER
            1->{
                Gravity.CENTER_VERTICAL or Gravity.START
            }
            else->{
                Gravity.CENTER_VERTICAL or Gravity.END
            }
        }
    // 动态计算X轴偏移（基于screenPos和targetOffsetDp）
    protected open val defaultXOffset: Int
        get() = if (screenPos>0&&targetOffsetDp > 0) calculateXPixelOffset(targetOffsetDp) else 0
    protected open val defaultYOffset = 0

    protected var btnOk:TextView?=null
    protected var btnCancel:TextView?=null
    protected var ivClose:ImageView?=null


    protected abstract fun getLayoutResId(): Int

    protected abstract fun initView(contentView: View)

    open fun builder(): BaseDialog {
        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(getLayoutResId())
            window?.apply {
                // 透明背景
                setBackgroundDrawableResource(android.R.color.transparent)
                // 宽度/位置/偏移配置
                attributes = attributes.apply {
                    width = defaultWidth
                    gravity = defaultGravity
                    x = defaultXOffset
                    y = defaultYOffset
                }
            }
            // 点击外部是否关闭（默认true）
            setCanceledOnTouchOutside(true)
            // 点击返回键是否关闭（默认true）
            setCancelable(true)
        }

        dialog?.findViewById<View>(android.R.id.content)?.let {
            btnOk = it.findViewById(R.id.tv_ok)
            btnCancel = it.findViewById(R.id.tv_cancel)
            btnCancel?.setOnClickListener { dismiss() }
            ivClose=it.findViewById(R.id.iv_close)
            ivClose?.setOnClickListener {dismiss() }
            initView(it)
        }

        show()
        return this
    }


    open fun show() {
        dialog?.show()
    }

    open fun dismiss() {
        dialog?.dismiss()
    }

    open fun release() {
        dialog?.apply {
            if (isShowing) dismiss()
        }
        dialog = null
    }

    fun setCanceledOnTouchOutside(cancel: Boolean): BaseDialog {
        dialog?.setCanceledOnTouchOutside(cancel)
        return this
    }

    fun setCancelable(cancel: Boolean): BaseDialog {
        dialog?.setCancelable(cancel)
        return this
    }

    // DP转PX工具方法
    protected fun dp2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    // 像素值计算辅助方法（适配屏幕宽度）
    protected fun calculateXPixelOffset(targetDp: Float): Int {
        return (Constants.WIDTH - dp2px(targetDp)) / 2
    }
}
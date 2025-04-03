package com.bll.lnkwrite.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.dialog.ImageDialog
import com.bll.lnkwrite.manager.CalenderDaoManager
import com.bll.lnkwrite.mvp.model.CalenderItemBean
import com.bll.lnkwrite.ui.adapter.CalenderMyAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_title.tv_setting
import org.greenrobot.eventbus.EventBus
import java.io.File

class CalenderMyActivity:BaseActivity(){

    private var items= mutableListOf<CalenderItemBean>()
    private var mAdapter:CalenderMyAdapter?=null
    private var position=-1

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
    }
    override fun initView() {
        setPageTitle(R.string.calender)
        showView(tv_setting)

        tv_setting.setText(R.string.set_calender)
        tv_setting.setOnClickListener {
            if (position>=0){
                val item=items[position]
                CalenderDaoManager.getInstance().setSetFalse()
                item.isSet=true
                CalenderDaoManager.getInstance().insertOrReplace(item)
                showToast(R.string.set_success)
                EventBus.getDefault().post(Constants.CALENDER_SET_EVENT)
            }
        }

        initRecycleView()
        fetchData()
    }

    private fun initRecycleView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,30f), DP2PX.dip2px(this,60f),
            DP2PX.dip2px(this,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = CalenderMyAdapter(R.layout.item_calender_my ,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list.addItemDecoration(SpaceGridItemDeco(4,  90))
            setOnItemClickListener { adapter, view, position ->
                val item=items[position]
                val urls=item.previewUrl.split(",")
                ImageDialog(this@CalenderMyActivity,urls).builder()
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@CalenderMyActivity.position=position
                if (view.id==R.id.cb_check){
                    for (item in items){
                        item.isCheck=false
                    }
                    val item=items[position]
                    item.isCheck=true
                    mAdapter?.notifyDataSetChanged()
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                delete(position)
                true
            }
        }
    }

    private fun delete(pos:Int){
        CommonDialog(this).setContent(R.string.tips_is_delete).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val item=items[pos]
                FileUtils.deleteFile(File(item.path))
                CalenderDaoManager.getInstance().deleteBean(item)
                mAdapter?.remove(pos)
                if (item.isSet)
                    EventBus.getDefault().post(Constants.CALENDER_SET_EVENT)
            }
        })
    }

    override fun fetchData() {
        val count=CalenderDaoManager.getInstance().queryList().size
        items=CalenderDaoManager.getInstance().queryList(pageIndex,pageSize)
        setPageNumber(count)
        for (item in items){
            if (item.isSet){
                item.isCheck=true
            }
        }
        mAdapter?.setNewData(items)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.CALENDER_EVENT){
            pageIndex=1
            fetchData()
        }
    }

}
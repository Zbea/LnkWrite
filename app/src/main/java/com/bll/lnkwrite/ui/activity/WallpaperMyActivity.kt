package com.bll.lnkwrite.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.dialog.ImageDialog
import com.bll.lnkwrite.manager.WallpaperDaoManager
import com.bll.lnkwrite.mvp.model.WallpaperBean
import com.bll.lnkwrite.ui.adapter.WallpaperMyAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_title.tv_setting
import java.io.File

class WallpaperMyActivity:BaseActivity(){

    private var items= mutableListOf<WallpaperBean>()
    private var mAdapter:WallpaperMyAdapter?=null
    private var position=-1

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
    }
    override fun initView() {
        setPageTitle(R.string.wallpaper_str)
        showView(tv_setting)

        tv_setting.setText(R.string.set_wallpaper)
        tv_setting.setOnClickListener {
            if (position>=0){
                val item=items[position]
                if(File(item.paths[0]).exists())
                    android.os.SystemProperties.set("xsys.eink.standby",item.paths[0])
                if(File(item.paths[1]).exists())
                    android.os.SystemProperties.set("xsys.eink.standby1",item.paths[1])
                showToast(R.string.set_success)
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

        rv_list.layoutManager = GridLayoutManager(this, 2)//创建布局管理
        mAdapter = WallpaperMyAdapter(R.layout.item_wallpaper_my,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco(2,90))
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(this@WallpaperMyActivity,items[position].bodyUrl.split(",")).builder()
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@WallpaperMyActivity.position=position
                if (view.id==R.id.cb_check){
                    for (item in items){
                        item.isCheck=false
                    }
                    val item=items[position]
                    item.isCheck=true
                    mAdapter?.notifyDataSetChanged()
                }
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
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
                WallpaperDaoManager.getInstance().deleteBean(item)
                val path= FileAddress().getPathImage("wallpaper" ,item.contentId)
                FileUtils.deleteFile(File(path))
                mAdapter?.remove(pos)
            }
        })
    }

    override fun fetchData() {
        val count=WallpaperDaoManager.getInstance().queryList().size
        items=WallpaperDaoManager.getInstance().queryList(pageSize,pageIndex)
        setPageNumber(count)
        mAdapter?.setNewData(items)
    }

}
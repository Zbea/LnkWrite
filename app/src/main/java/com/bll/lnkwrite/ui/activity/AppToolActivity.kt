package com.bll.lnkwrite.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.manager.AppDaoManager
import com.bll.lnkwrite.mvp.model.AppBean
import com.bll.lnkwrite.ui.adapter.AppListAdapter
import com.bll.lnkwrite.utils.AppUtils
import com.bll.lnkwrite.utils.BitmapUtils
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_app_tool.*
import kotlinx.android.synthetic.main.common_title.tv_ok

class AppToolActivity:BaseActivity() {

    private var apps= mutableListOf<AppBean>()
    private var menuApps= mutableListOf<AppBean>()
    private var mAdapter: AppListAdapter?=null
    private var mMenuAdapter: AppListAdapter?=null

    override fun layoutId(): Int {
        return R.layout.ac_app_tool
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle(R.string.Toolkit)
        showView(tv_ok)
        tv_ok.setText(R.string.add)

        initRecyclerView()
        initMenuRecyclerView()

        tv_ok.setOnClickListener {
            for (item in apps){
                if (item.isCheck){
                    if (!AppDaoManager.getInstance().isExist(item.packageName,2)){
                        item.type=2
                        AppDaoManager.getInstance().insertOrReplace(item)
                    }
                }
            }
            setData()
            setMenuData()
        }
        tv_out.setOnClickListener {
            for (item in menuApps){
                if (item.isCheck){
                    item.type=0
                    AppDaoManager.getInstance().insertOrReplace(item)
                }
            }
            setData()
            setMenuData()
        }

        setData()
        setMenuData()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mAdapter = AppListAdapter(R.layout.item_app_list, 1,null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(6,50))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val packageName= apps[position].packageName
            if (packageName!=Constants.PACKAGE_GEOMETRY){
                AppUtils.startAPP(this,packageName)
            }
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=apps[position]
            if (view.id==R.id.ll_name){
                item.isCheck=!item.isCheck
                mAdapter?.notifyItemChanged(position)
            }
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            val item=apps[position]
            val packageName= item.packageName
            if (packageName!=Constants.PACKAGE_GEOMETRY){
                CommonDialog(this).setContent(R.string.tips_is_uninstall).builder().setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        AppUtils.uninstallAPK(this@AppToolActivity,apps[position].packageName)
                        AppDaoManager.getInstance().delete(item)
                        return
                    }
                })
            }
            true
        }
    }

    private fun initMenuRecyclerView(){
        rv_list_tool.layoutManager = GridLayoutManager(this,6)//创建布局管理
        mMenuAdapter = AppListAdapter(R.layout.item_app_list, 1,null)
        rv_list_tool.adapter = mMenuAdapter
        mMenuAdapter?.bindToRecyclerView(rv_list_tool)
        rv_list_tool.addItemDecoration(SpaceGridItemDeco(6,30))
        mMenuAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=menuApps[position]
            if (view.id==R.id.ll_name){
                item.isCheck=!item.isCheck
                mMenuAdapter?.notifyItemChanged(position)
            }
        }
    }

    private fun setData(){
        apps.clear()
        if (!AppDaoManager.getInstance().isExist(Constants.PACKAGE_GEOMETRY)){
            AppDaoManager.getInstance().insertOrReplace(AppBean().apply {
                appName=getString(R.string.geometry_title_str)
                imageByte = BitmapUtils.drawableToByte(getDrawable(R.mipmap.icon_app_geometry))
                packageName=Constants.PACKAGE_GEOMETRY
                type=0
                subType=1
            })
        }
        apps=AppDaoManager.getInstance().queryAPPTool()
        for (item in apps){
            item.isCheck=false
        }
        mAdapter?.setNewData(apps)
    }

    private fun setMenuData(){
        menuApps=AppDaoManager.getInstance().queryTool()
        for (item in menuApps){
            item.isCheck=false
        }
        mMenuAdapter?.setNewData(menuApps)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.APP_INSTALL_INSERT_EVENT->{
                setData()
            }
            Constants.APP_UNINSTALL_EVENT->{
                setData()
                setMenuData()
            }
        }
    }

}
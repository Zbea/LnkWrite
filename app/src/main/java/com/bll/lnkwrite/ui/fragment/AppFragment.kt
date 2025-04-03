package com.bll.lnkwrite.ui.fragment

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.manager.AppDaoManager
import com.bll.lnkwrite.mvp.model.AppBean
import com.bll.lnkwrite.ui.adapter.AppListAdapter
import com.bll.lnkwrite.utils.AppUtils
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_list_tab.rv_list
import kotlinx.android.synthetic.main.fragment_list_tab.rv_tab

class AppFragment:BaseFragment() {

    private var apps= mutableListOf<AppBean>()
    private var mAdapter: AppListAdapter?=null
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        setTitle(R.string.app)
        disMissView(rv_tab)

        initRecyclerView()

    }

    override fun lazyLoad() {
        initData()
    }


    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(requireActivity(),30f), DP2PX.dip2px(requireActivity(),30f), DP2PX.dip2px(requireActivity(),30f),20)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity,5)//创建布局管理
        mAdapter = AppListAdapter(R.layout.item_app_list, 0,null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(5,50))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val packageName= apps[position].packageName
            AppUtils.startAPP(activity,packageName)
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            this.position=position
            CommonDialog(requireActivity(),1).setContent(R.string.tips_is_uninstall).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun ok() {
                    AppUtils.uninstallAPK(requireActivity(),apps[position].packageName)
                }
            })
            true
        }
    }


    private fun initData() {
        apps=AppUtils.scanLocalInstallAppList(requireActivity())
        mAdapter?.setNewData(apps)
    }


    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.APP_INSTALL_EVENT->{
                initData()
            }
            Constants.APP_UNINSTALL_EVENT->{
                AppDaoManager.getInstance().delete(apps[position].packageName)
                mAdapter?.remove(position)
            }
        }
    }

}
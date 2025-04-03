package com.bll.lnkwrite.ui.fragment

import PopupClick
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.InputContentDialog
import com.bll.lnkwrite.dialog.ItemSelectorDialog
import com.bll.lnkwrite.dialog.LongClickManageDialog
import com.bll.lnkwrite.dialog.ScreenshotDetailsDialog
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.mvp.model.ItemList
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.mvp.model.PopupBean
import com.bll.lnkwrite.ui.activity.ScreenshotManagerActivity
import com.bll.lnkwrite.ui.adapter.ScreenshotAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_list_tab.rv_list
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import java.io.File

class ScreenShotFragment: BaseFragment() {

    private var popupBeans = mutableListOf<PopupBean>()
    private var longBeans = mutableListOf<ItemList>()
    private var tabPos=0
    private var mAdapter: ScreenshotAdapter?=null
    private var position=0
    private var totalNum=0
    private var tabPath=""

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }
    override fun initView() {
        setTitle(R.string.screenshot)
        pageSize=12
        showView(iv_manager)

        popupBeans.add(PopupBean(0, getString(R.string.type_manager_str), false))
        popupBeans.add(PopupBean(1, getString(R.string.type_create_str), false))
        popupBeans.add(PopupBean(2,  getString(R.string.screenshot_details_str), false))

        iv_manager.setOnClickListener {
            PopupClick(requireActivity(), popupBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        customStartActivity(Intent(requireActivity(), ScreenshotManagerActivity::class.java))
                    }
                    1 -> {
                        InputContentDialog(requireActivity(),2, getString(R.string.type_create_str)).builder().setOnDialogClickListener {
                            if (ItemTypeDaoManager.getInstance().isExist(it, 1)) {
                                showToast(R.string.existed)
                                return@setOnDialogClickListener
                            }
                            val path = FileAddress().getPathScreen(it)
                            if (!File(path).exists()) {
                                FileUtils.mkdirs(path)
                            }
                            val bean = ItemTypeBean()
                            bean.type =3
                            bean.title = it
                            bean.path = path
                            bean.date = System.currentTimeMillis()
                            ItemTypeDaoManager.getInstance().insertOrReplace(bean)
                            mTabTypeAdapter?.addData(bean)
                        }
                    }
                    2->{
                        ScreenshotDetailsDialog(requireActivity()).builder()
                    }
                }
            }
        }

        initRecycleView()
        initTab()
    }
    override fun lazyLoad() {
    }

    private fun initTab() {
        pageIndex=1
        itemTabTypes= ItemTypeDaoManager.getInstance().queryAll(3)
        itemTabTypes.add(0, MethodManager.getDefaultItemTypeScreenshot())
        if (tabPos>=itemTabTypes.size){
            tabPos=0
        }
        itemTabTypes=MethodManager.setItemTypeBeanCheck(itemTabTypes,tabPos)
        mTabTypeAdapter?.setNewData(itemTabTypes)
        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        pageIndex=1
        tabPos=position
        fetchData()
    }

    private fun initRecycleView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),20f), DP2PX.dip2px(requireActivity(),30f),
            DP2PX.dip2px(requireActivity(),20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(requireActivity(), 3)//创建布局管理
        mAdapter = ScreenshotAdapter(R.layout.item_textbook, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(3,  40))
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val index=totalNum-1-((pageIndex-1)*pageSize+position)
                MethodManager.gotoScreenFile(requireActivity(),index,tabPath)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@ScreenShotFragment.position=position
                onLongClick()
                true
            }
        }
    }

    private fun onLongClick() {
        longBeans.clear()
        longBeans.add(ItemList().apply {
            name=getString(R.string.delete)
            resId=R.mipmap.icon_setting_delete
        })
        if (tabPos==0){
            longBeans.add(ItemList().apply {
                name=getString(R.string.set)
                resId=R.mipmap.icon_setting_set
            })
        }
        else{
            longBeans.add(ItemList().apply {
                name=getString(R.string.shift_out)
                resId=R.mipmap.icon_setting_out
            })
        }
        val file= mAdapter?.data?.get(position)!!
        LongClickManageDialog(requireActivity(),2,file.name,longBeans).builder()
            .setOnDialogClickListener {
                if (it==0){
                    mAdapter?.remove(position)
                    FileUtils.deleteFile(file)
                    val drawPath=tabPath+"/drawing/${file.name}"
                    FileUtils.delete(drawPath)
                }
                else{
                    if (tabPos==0){
                        val types= ItemTypeDaoManager.getInstance().queryAll(3)
                        val lists= mutableListOf<ItemList>()
                        for (ite in types){
                            lists.add(ItemList(types.indexOf(ite),ite.title))
                        }
                        ItemSelectorDialog(requireActivity(),2,getString(R.string.type_set_str),lists).builder().setOnDialogClickListener{ pos->
                            FileUtils.copyFile(file.path,types[pos].path+"/"+file.name)
                            mAdapter?.remove(position)
                        }
                    }
                    else{
                        val path= FileAddress().getPathScreen(getString(R.string.untype))
                        FileUtils.copyFile(file.path,path+"/"+file.name)
                        mAdapter?.remove(position)
                    }
                }
            }
    }

    override fun fetchData() {
        tabPath=itemTabTypes[tabPos].path
        totalNum= FileUtils.getDescFiles(tabPath).size
        setPageNumber(totalNum)
        val files= FileUtils.getDescFiles(tabPath,pageIndex, pageSize)
        mAdapter?.setNewData(files)
    }

    override fun onRefreshData() {
        fetchData()
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag== Constants.SCREENSHOT_MANAGER_EVENT){
            initTab()
        }
    }

}
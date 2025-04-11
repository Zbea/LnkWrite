package com.bll.lnkwrite.ui.fragment

import PopupClick
import android.media.MediaScannerConnection
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.dialog.DocumentDetailsDialog
import com.bll.lnkwrite.dialog.InputContentDialog
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.mvp.model.PopupBean
import com.bll.lnkwrite.ui.adapter.DocumentAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_list_tab.rv_list
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import java.io.File


class DocumentFragment : BaseFragment() {
    private var popupBeans = mutableListOf<PopupBean>()
    private var tabPos = 0
    private var mAdapter: DocumentAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        setTitle(R.string.document)
        showView(iv_manager)
        pageSize = 9

        popupBeans.add(PopupBean(0, getString(R.string.type_create_str), false))
        popupBeans.add(PopupBean(1, getString(R.string.type_delete_str), false))
        popupBeans.add(PopupBean(2, getString(R.string.document_details_str), false))

        iv_manager?.setOnClickListener {
            PopupClick(requireActivity(), popupBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        InputContentDialog(requireActivity(), getString(R.string.type_create_str)).builder().setOnDialogClickListener {
                            if (ItemTypeDaoManager.getInstance().isExist(it, 6)) {
                                showToast(1, R.string.existed)
                                return@setOnDialogClickListener
                            }
                            val path = FileAddress().getPathDocument(it)
                            MethodManager.createFileScan(requireActivity(),path)

                            val itemTypeBean = ItemTypeBean()
                            itemTypeBean.type = 6
                            itemTypeBean.date = System.currentTimeMillis()
                            itemTypeBean.title = it
                            itemTypeBean.path = path
                            ItemTypeDaoManager.getInstance().insertOrReplace(itemTypeBean)
                            mTabTypeAdapter?.addData(itemTypeBean)
                        }
                    }
                    1 -> {
                        if (tabPos == 0) {
                            showToast(1, R.string.toast_type_default_no_delete)
                            return@setOnSelectListener
                        }
                        if (FileUtils.isExistContent(itemTabTypes[tabPos].path)) {
                            showToast(1, R.string.toast_type_exist_no_delete)
                            return@setOnSelectListener
                        }
                        CommonDialog(requireActivity(), 1).setContent(R.string.tips_is_delete).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun ok() {
                                val itemTypeBean=itemTabTypes[tabPos]
                                ItemTypeDaoManager.getInstance().deleteBean(itemTypeBean)
                                FileUtils.delete(itemTypeBean.path)
                                MediaScannerConnection.scanFile(requireActivity(), arrayOf(itemTypeBean.path),null, null)
                                mTabTypeAdapter?.remove(tabPos)
                                tabPos = 0
                                itemTabTypes[0].isCheck = true
                                pageIndex = 1
                                fetchData()
                            }
                        })
                    }
                    2 -> {
                        DocumentDetailsDialog(requireActivity()).builder()
                    }
                }
            }
        }

        initRecycleView()
        initTab()
    }

    override fun lazyLoad() {
        for (item in itemTabTypes){
            val path=item.path
            MethodManager.createFileScan(requireActivity(),path)
        }
        fetchData()
    }


    private fun initTab() {
        pageIndex = 1
        itemTabTypes = ItemTypeDaoManager.getInstance().queryAll(6)
        itemTabTypes.add(0, MethodManager.getDefaultItemTypeDocument())
        itemTabTypes = MethodManager.setItemTypeBeanCheck(itemTabTypes, tabPos)
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        tabPos = position
        pageIndex = 1
        fetchData()
    }

    private fun initRecycleView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(), 20f), DP2PX.dip2px(requireActivity(), 30f),
            DP2PX.dip2px(requireActivity(), 20f), 0
        )
        layoutParams.weight = 1f
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(requireActivity(), 3)//创建布局管理
        mAdapter = DocumentAdapter(R.layout.item_textbook, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(3, 40))
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val file = mAdapter?.data?.get(position)
                MethodManager.gotoDocument(requireActivity(), file!!)
            }
            setOnItemLongClickListener { adapter, view, position ->
                val file = mAdapter?.data?.get(position)
                val fileName = FileUtils.getUrlName(file?.path)
                val drawPath = file?.parent + "/${fileName}draw/"
                CommonDialog(requireActivity(),1).setContent(R.string.tips_is_delete).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun ok() {
                            FileUtils.deleteFile(file)
                            FileUtils.deleteFile(File(drawPath))
                            MediaScannerConnection.scanFile(requireActivity(), arrayOf(file?.absolutePath),null, null)
                            fetchData()
                        }
                    })
                true
            }
        }
    }

    override fun fetchData() {
        val path = itemTabTypes[tabPos].path

        val totalNum = FileUtils.getFiles(path).size
        setPageNumber(totalNum)
        val files = FileUtils.getDescFiles(path, pageIndex, pageSize)
        mAdapter?.setNewData(files)
    }


    override fun onRefreshData() {
        lazyLoad()
    }
}
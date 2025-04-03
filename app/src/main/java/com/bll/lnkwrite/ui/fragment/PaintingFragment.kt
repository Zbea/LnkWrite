package com.bll.lnkwrite.ui.fragment

import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.dialog.InputContentDialog
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.manager.PaintingContentDaoManager
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.ui.activity.drawing.PaintingDrawingActivity
import com.bll.lnkwrite.ui.adapter.PaintingAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import kotlinx.android.synthetic.main.fragment_list_tab.rv_list
import kotlinx.android.synthetic.main.fragment_list_tab.rv_tab
import java.io.File

class PaintingFragment: BaseFragment() {

    private var mAdapter: PaintingAdapter?=null
    private var items= mutableListOf<ItemTypeBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }
    override fun initView() {
        setTitle(R.string.painting)
        pageSize=9
        disMissView(rv_tab)
        showView(iv_manager)
        iv_manager.setImageResource(R.mipmap.icon_add)

        iv_manager.setOnClickListener {
            InputContentDialog(requireActivity(),2,getString(R.string.input_painting_title)).builder().setOnDialogClickListener{
                if (ItemTypeDaoManager.getInstance().isExist(it,5)){
                    showToast(R.string.existed)
                }
                else{
                    val item = ItemTypeBean()
                    item.type=5
                    item.title = it
                    item.date=System.currentTimeMillis()
                    ItemTypeDaoManager.getInstance().insertOrReplace(item)

                    fetchData()
                }
            }
        }

        initRecyclerView()
    }
    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(requireActivity(),20f), DP2PX.dip2px(requireActivity(),70f), DP2PX.dip2px(requireActivity(),20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = PaintingAdapter(R.layout.item_painting, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 3)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list.addItemDecoration(SpaceGridItemDeco(3, 100))
            setOnItemClickListener { adapter, view, position ->
                val intent = Intent(context, PaintingDrawingActivity::class.java)
                intent.putExtra("paintingType", items[position].title)
                intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true)
                customStartActivity(intent)
            }
            setOnItemLongClickListener { adapter, view, position ->
                CommonDialog(requireActivity(),2).setContent(R.string.tips_is_delete).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            delete(position)
                        }
                    })
                true
            }
        }
    }

    private fun delete(pos:Int){
        val item=items[pos]
        FileUtils.deleteFile(File(FileAddress().getPathPainting(item.title)))
        PaintingContentDaoManager.getInstance().deleteType(item.title)
        ItemTypeDaoManager.getInstance().deleteBean(item)
        fetchData()
        showToast(R.string.delete_success)
    }

    override fun fetchData() {
        val count=ItemTypeDaoManager.getInstance().queryAll(5).size
        setPageNumber(count)
        items=ItemTypeDaoManager.getInstance().queryAllOrderDesc(5,pageIndex,pageSize)
        mAdapter?.setNewData(items)
    }
}
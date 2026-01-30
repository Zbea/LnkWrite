package com.bll.lnkwrite.ui.fragment

import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.dialog.InputContentDialog
import com.bll.lnkwrite.dialog.ItemSelectorDialog
import com.bll.lnkwrite.dialog.LongClickManageDialog
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.manager.PaintingContentDaoManager
import com.bll.lnkwrite.mvp.model.CloudListBean
import com.bll.lnkwrite.mvp.model.ItemList
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.ui.activity.drawing.PaintingDrawingActivity
import com.bll.lnkwrite.ui.adapter.PaintingAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.FileUploadManager
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.utils.NetworkUtil
import com.bll.lnkwrite.utils.ToolUtils
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import kotlinx.android.synthetic.main.fragment_list_tab.rv_list
import kotlinx.android.synthetic.main.fragment_list_tab.rv_tab
import java.io.File

class PaintingFragment: BaseFragment() {
    private var longBeans = mutableListOf<ItemList>()
    private var mAdapter: PaintingAdapter?=null
    private var items= mutableListOf<ItemTypeBean>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }
    override fun initView() {
        setTitle(R.string.painting)

        longBeans.add(ItemList().apply {
            name=getString(R.string.delete)
            resId=R.mipmap.icon_setting_delete
        })
        longBeans.add(ItemList().apply {
            name=getString(R.string.upload)
            resId=R.mipmap.icon_upload
        })

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
                    item.typeId=ToolUtils.getDateId()
                    item.path=FileAddress().getPathPainting(item.typeId)
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
                intent.putExtra("paintingType", items[position].typeId)
                intent.putExtra(Constants.INTENT_DRAWING_FOCUS, true)
                customStartActivity(intent)
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@PaintingFragment.position=position
                onLongClick()
                true
            }
        }
    }

    private fun onLongClick() {
        val item=items[position]
        LongClickManageDialog(requireActivity(),2,item.title,longBeans).builder()
            .setOnDialogClickListener {
                if (it==0){
                    delete()
                    showToast(R.string.delete_success)
                }
                else{
                    if (FileUtils.isExistContent(item.path)){
                        if (NetworkUtil.isNetworkConnected()){
                            mQiniuPresenter.getToken()
                        }
                        else{
                            showToast(R.string.net_work_error)
                        }
                    }
                    else{
                        showToast(2,R.string.toast_content_null_no_upload)
                    }
                }
            }
    }

    private fun delete(){
        val item=items[position]
        FileUtils.deleteFile(File(item.path))
        PaintingContentDaoManager.getInstance().deleteType(item.typeId)
        ItemTypeDaoManager.getInstance().deleteBean(item)
        fetchData()
    }

    override fun fetchData() {
        val count=ItemTypeDaoManager.getInstance().queryAll(5).size
        setPageNumber(count)
        items=ItemTypeDaoManager.getInstance().queryAllOrderDesc(5,pageIndex,pageSize)
        mAdapter?.setNewData(items)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.PAINTING_TYPE_EVENT){
            fetchData()
        }
    }

    override fun onUploadToken(token: String) {
        showLoading()
        val item=items[position]
        val contents=PaintingContentDaoManager.getInstance().queryAll(item.typeId)
        FileUploadManager(token).apply {
            setCallBack(object : FileUploadManager.UploadCallBack {
                override fun onUploadSuccess(url: String) {
                    cloudList.add(CloudListBean().apply {
                        type=7
                        subTypeStr=getString(R.string.painting)
                        date=System.currentTimeMillis()
                        listJson= Gson().toJson(item)
                        contentJson=Gson().toJson(contents)
                        downloadUrl=url
                    })
                    mCloudUploadPresenter.upload(cloudList)
                }
                override fun onUploadFail() {
                }
            })
            startZipUpload(item.path,item.title)
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        delete()
    }

}
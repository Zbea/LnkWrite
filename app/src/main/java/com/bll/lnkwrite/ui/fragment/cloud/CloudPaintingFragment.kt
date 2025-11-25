package com.bll.lnkwrite.ui.fragment.cloud

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseCloudFragment
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.manager.PaintingContentDaoManager
import com.bll.lnkwrite.mvp.model.CloudList
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.mvp.model.PaintingContentBean
import com.bll.lnkwrite.ui.adapter.cloud.CloudScreenshotAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.DateUtils
import com.bll.lnkwrite.utils.DownloadManager
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.utils.zip.IZipCallback
import com.bll.lnkwrite.utils.zip.ZipUtils
import com.bll.lnkwrite.widget.SpaceItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_list_tab.rv_list
import org.greenrobot.eventbus.EventBus
import java.io.File

class CloudPaintingFragment: BaseCloudFragment() {
    private var mAdapter: CloudScreenshotAdapter?=null
    private var items= mutableListOf<ItemTypeBean>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=14
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,30f), DP2PX.dip2px(activity,20f), DP2PX.dip2px(activity,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        mAdapter = CloudScreenshotAdapter(R.layout.item_cloud_diary, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                this@CloudPaintingFragment.position=position
                CommonDialog(requireActivity()).setContent(R.string.tips_is_download).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            download(items[position])
                        }
                    })
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@CloudPaintingFragment.position=position
                if (view.id==R.id.iv_delete){
                    CommonDialog(requireActivity()).setContent(R.string.tips_is_delete).builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                deleteItem()
                            }
                        })
                }
            }
        }
        rv_list.addItemDecoration(SpaceItemDeco(30))
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(items[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    private fun download(item: ItemTypeBean){
        showLoading()
        val zipPath = FileAddress().getPathZip(DateUtils.longToString(item.date))
        mDownloadManager?.startSingle(item.downloadUrl,zipPath, object : DownloadManager.SingleCallback {
            override fun onProgress(task: BaseDownloadTask, soFar: Long, total: Long) {
            }
            override fun onCompleted(task: BaseDownloadTask) {
                ZipUtils.unzip1(zipPath, item.path, object : IZipCallback {
                    override fun onFinish() {
                        item.id=null
                        item.date=System.currentTimeMillis()
                        ItemTypeDaoManager.getInstance().insertOrReplace(item)

                        val contents=Gson().fromJson(item.contentJson, object : TypeToken<List<PaintingContentBean>>() {}.type) as MutableList<PaintingContentBean>
                        for (contentBean in contents){
                            contentBean.id=null
                            PaintingContentDaoManager.getInstance().insertOrReplace(contentBean)
                        }

                        FileUtils.deleteFile(File(zipPath))
                        showToast(R.string.download_success)
                        EventBus.getDefault().post(Constants.PAINTING_TYPE_EVENT)
                        deleteItem()
                        hideLoading()
                    }
                    override fun onProgress(percentDone: Int) {
                    }
                    override fun onError(msg: String?) {
                        showToast(msg!!)
                        hideLoading()
                    }
                    override fun onStart() {
                    }
                })
            }
            override fun onFailed(task: BaseDownloadTask?, error: String) {
                hideLoading()
                showToast(R.string.download_fail)
            }
        })
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 7
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        items.clear()
        for (item in cloudList.list){
            if (item.listJson.isNotEmpty()){
                val itemTypeBean= Gson().fromJson(item.listJson, ItemTypeBean::class.java)
                itemTypeBean.cloudId=item.id
                itemTypeBean.downloadUrl=item.downloadUrl
                itemTypeBean.contentJson=item.contentJson
                items.add(itemTypeBean)
            }
        }
        mAdapter?.setNewData(items)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
        onRefreshList(items)
    }
}
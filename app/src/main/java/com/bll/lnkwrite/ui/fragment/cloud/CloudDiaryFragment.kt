package com.bll.lnkwrite.ui.fragment.cloud

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseCloudFragment
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.manager.DiaryDaoManager
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.mvp.model.CloudList
import com.bll.lnkwrite.mvp.model.CloudListBean
import com.bll.lnkwrite.mvp.model.DiaryBean
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.ui.adapter.CloudDiaryAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.DateUtils
import com.bll.lnkwrite.utils.FileDownManager
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.utils.zip.IZipCallback
import com.bll.lnkwrite.utils.zip.ZipUtils
import com.bll.lnkwrite.widget.SpaceItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.*
import java.io.File

class CloudDiaryFragment: BaseCloudFragment() {
    private var mAdapter: CloudDiaryAdapter?=null
    private var items= mutableListOf<CloudListBean>()
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
        mAdapter = CloudDiaryAdapter(R.layout.item_cloud_diary, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                this@CloudDiaryFragment.position=position
                CommonDialog(requireActivity()).setContent(R.string.tips_is_download).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            downloadItem()
                        }
                    })
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@CloudDiaryFragment.position=position
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

    private fun downloadItem(){
        val item=items[position]
        if (!ItemTypeDaoManager.getInstance().isExistDiaryType(item.id)){
            showLoading()
            download(item)
        }
        else{
            showToast(R.string.downloaded)
        }
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(items[position].id)
        mCloudPresenter.deleteCloud(ids)
    }

    private fun download(item:CloudListBean){
        val fileName=DateUtils.longToStringCalender(item.date)
        val zipPath = FileAddress().getPathZip(fileName)
        val fileTargetPath= File(FileAddress().getPathDiary(fileName)).parent
        FileDownManager.with(activity).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, fileTargetPath, object : IZipCallback {
                        override fun onFinish() {
                            val itemTypeBean= ItemTypeBean().apply {
                                type=4
                                title=item.title
                                date=System.currentTimeMillis()
                                typeId=item.id
                            }
                            ItemTypeDaoManager.getInstance().insertOrReplace(itemTypeBean)

                            val diaryBeans: MutableList<DiaryBean> = Gson().fromJson(item.listJson, object : TypeToken<List<DiaryBean>>() {}.type)
                            for (diaryBean in diaryBeans){
                                diaryBean.id=null//设置数据库id为null用于重新加入
                                diaryBean.isUpload=true
                                diaryBean.uploadId=item.id
                                DiaryDaoManager.getInstance().insertOrReplace(diaryBean)
                            }

                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            showToast(R.string.download_success)
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
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(R.string.download_fail)
                }
            })
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 4
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        items=cloudList.list
        mAdapter?.setNewData(items)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
        onRefreshList(items)
    }
}
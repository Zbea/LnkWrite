package com.bll.lnkwrite.ui.fragment.resource

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.ImageDialog
import com.bll.lnkwrite.manager.WallpaperDaoManager
import com.bll.lnkwrite.mvp.model.WallpaperBean
import com.bll.lnkwrite.mvp.model.WallpaperList
import com.bll.lnkwrite.mvp.presenter.WallpaperPresenter
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.ui.adapter.WallpaperAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.FileMultitaskDownManager
import com.bll.lnkwrite.utils.NetworkUtil
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.*

class WallpaperDownloadFragment : BaseFragment(), IContractView.IWallpaperView{

    private var presenter= WallpaperPresenter(this)
    private var items= mutableListOf<WallpaperBean>()
    private var mAdapter: WallpaperAdapter?=null
    private var supply=1
    private var position=0

    override fun onList(bean: WallpaperList) {
        setPageNumber(bean.total)
        items=bean.list
        mAdapter?.setNewData(items)
    }

    override fun buySuccess() {
        items[position].buyStatus=1
        mAdapter?.notifyDataSetChanged()
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=6
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected()) {
            fetchData()
        }
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),30f),
            DP2PX.dip2px(requireActivity(),40f),
            DP2PX.dip2px(requireActivity(),30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(requireActivity(),2)//创建布局管理
        mAdapter = WallpaperAdapter(R.layout.item_wallpaper, items)
        rv_list.adapter = mAdapter
        rv_list.addItemDecoration(SpaceGridItemDeco(2,30))
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            ImageDialog(requireActivity(), arrayListOf(items[position].bodyUrl) ).builder()
        }
        mAdapter?.setOnItemChildClickListener{ adapter, view, position ->
            this.position=position
            val item=items[position]
            if (view.id==R.id.btn_download){
                if (item.buyStatus==1){
                    val paintingBean= WallpaperDaoManager.getInstance().queryBean(item.contentId)
                    if (paintingBean==null){
                        onDownload()
                    }
                    else{
                        showToast(R.string.downloaded)
                    }
                }
                else{
                    val map = HashMap<String, Any>()
                    map["type"] = 5
                    map["bookId"] = item.contentId
                    presenter.onBuy(map)
                }
            }
        }
    }


    /**
     * 下载
     */
    private fun onDownload(){
        val item=items[position]
        showLoading()
        val pathStr= FileAddress().getPathImage("wallpaper",item.contentId)
        val images = item.bodyUrl.split(",")
        val savePaths= arrayListOf("$pathStr/1.png","$pathStr/2.png")
        FileMultitaskDownManager.with(requireActivity()).create(images).setPath(savePaths).startMultiTaskDownLoad(
            object : FileMultitaskDownManager.MultiTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, ) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    hideLoading()
                    item.paths=savePaths
                    item.date=System.currentTimeMillis()
                    WallpaperDaoManager.getInstance().insertOrReplace(item)
                    showToast(R.string.download_success)
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(R.string.download_fail)
                }
            })
    }

    /**
     * 改变供应商
     */
    fun changeSupply(supply:Int){
        this.supply=supply
        fetchData()
    }


    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["supply"]=supply
        map["type"]=1
        map["imgType"]=2
        presenter.getList(map)
    }

}
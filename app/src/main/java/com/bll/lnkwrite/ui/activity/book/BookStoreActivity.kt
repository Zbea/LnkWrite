package com.bll.lnkwrite.ui.activity.book

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.FileAddress
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.DownloadBookDialog
import com.bll.lnkwrite.dialog.PopupRadioList
import com.bll.lnkwrite.manager.BookDaoManager
import com.bll.lnkwrite.mvp.model.book.Book
import com.bll.lnkwrite.mvp.model.book.BookStore
import com.bll.lnkwrite.mvp.model.book.BookStoreType
import com.bll.lnkwrite.mvp.model.*
import com.bll.lnkwrite.mvp.presenter.BookStorePresenter
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.ui.adapter.BookAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.FileBigDownManager
import com.bll.lnkwrite.utils.FileUtils
import com.bll.lnkwrite.utils.MD5Utils
import com.bll.lnkwrite.utils.NetworkUtil
import com.bll.lnkwrite.utils.ToolUtils
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_list_tab.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * 书城
 */
class BookStoreActivity : BaseActivity(), IContractView.IBookStoreView {

    private var tabStr=""
    private var type=0
    private val presenter = BookStorePresenter(this)
    private var books = mutableListOf<Book>()
    private var mAdapter: BookAdapter? = null
    private var grade = 0
    private var subTypeStr = ""//子类
    private var subType=0
    private var downloadBookDialog: DownloadBookDialog? = null
    private var position=0
    private var popGrades= mutableListOf<PopupBean>()
    private var subTypeList = mutableListOf<ItemList>()
    private var popSupplys = mutableListOf<PopupBean>()
    private var supply=0

    override fun onBook(bookStore: BookStore) {
        setPageNumber(bookStore.total)
        books = bookStore.list
        mAdapter?.setNewData(books)
    }

    override fun onType(bookStoreType: BookStoreType) {
        val types = bookStoreType.subType[tabStr]
        if (types?.size!! >0){
            subTypeList=types
            subTypeStr=types[0].desc
            subType=types[0].type
            initTab()
        }
        fetchData()
    }

    override fun buyBookSuccess() {
        books[position].buyStatus=1
        downloadBookDialog?.setChangeStatus()
        mAdapter?.notifyItemChanged(position)
    }


    override fun layoutId(): Int {
        return R.layout.ac_list_tab
    }

    override fun initData() {
        pageSize=12
        type = intent.flags
        tabStr= DataBeanManager.bookStoreTypes[type-1].name
        popGrades=DataBeanManager.popupTypeGrades
        popSupplys=DataBeanManager.popupSupplys
        supply=popSupplys[0].id

        if (NetworkUtil.isNetworkConnected())
            presenter.getBookType()
    }


    override fun initView() {
        setPageTitle(tabStr)
        showView(tv_subgrade,tv_supply)

        if (popGrades.size>0){
            grade = popGrades[0].id
            initSelectorView()
        }

        initRecyclerView()
    }

    /**
     * 设置分类选择
     */
    private fun initSelectorView() {
        tv_subgrade.text = popGrades[0].name
        tv_subgrade.setOnClickListener {
            PopupRadioList(this, popGrades, tv_subgrade,tv_subgrade.width, 5).builder()
            .setOnSelectListener { item ->
                grade = item.id
                tv_subgrade.text = item.name
                typeFindData()
            }
        }

        tv_supply.text = popSupplys[0].name
        tv_supply.setOnClickListener {
            PopupRadioList(this, popSupplys, tv_supply,tv_supply.width, 5).builder()
                .setOnSelectListener { item ->
                    supply = item.id
                    tv_supply.text = item.name
                    typeFindData()
                }
        }

    }

    private fun initTab(){
        for (i in subTypeList.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=subTypeList[i].desc
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        subTypeStr = subTypeList[position].desc
        subType=subTypeList[position].type
        typeFindData()
    }


    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,30f),DP2PX.dip2px(this,50f),DP2PX.dip2px(this,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_bookstore, books)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            showBookDetails(books[position])
        }
        rv_list?.addItemDecoration(SpaceGridItemDeco(4,60))
    }


    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: Book) {
        downloadBookDialog = DownloadBookDialog(this, book)
        downloadBookDialog?.builder()
        downloadBookDialog?.setOnClickListener {
            if (book.buyStatus==1){
                val localBook = BookDaoManager.getInstance().queryByBookID(book.bookId)
                if (localBook == null) {
                    downLoadStart(book.downloadUrl,book)
                } else {
                    book.loadSate =2
                    showToast(R.string.downloaded)
                    downloadBookDialog?.setDissBtn()
                }
            }
            else{
                val map = HashMap<String, Any>()
                map["type"] = 3
                map["bookId"] = book.bookId
                presenter.buyBook(map)
            }
        }
    }

    //下载book
    private fun downLoadStart(url: String,book: Book): BaseDownloadTask? {
        showLoading()
        val fileName = MD5Utils.digest(book.bookId.toString())//文件名
        val targetFileStr = FileAddress().getPathBook(fileName+ FileUtils.getUrlFormat(book.downloadUrl))
        val download = FileBigDownManager.with(this).create(url).setPath(targetFileStr)
            .startSingleTaskDownLoad(object :
                FileBigDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                    if (task != null && task.isRunning) {
                        runOnUiThread {
                            val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0M")+ "/"+
                                    ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                            downloadBookDialog?.setUnClickBtn(s)
                        }
                    }
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    book.apply {
                        loadSate = 2
                        subtypeStr = ""
                        time = System.currentTimeMillis()//下载时间用于排序
                        bookPath = targetFileStr
                        bookDrawPath=FileAddress().getPathBookDraw(fileName)
                    }
                    //下载解压完成后更新存储的book
                    BookDaoManager.getInstance().insertOrReplaceBook(book)
                    downloadBookDialog?.dismiss()
                    Handler().postDelayed({
                        hideLoading()
                        showToast(book.bookName+getString(R.string.download_success))
                    },500)
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(book.bookName+getString(R.string.download_fail))
                    downloadBookDialog?.setChangeStatus()
                }
            })
        return download
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

    private fun typeFindData(){
        pageIndex = 1
        fetchData()
    }

    override fun fetchData() {
        books.clear()
        mAdapter?.notifyDataSetChanged()

        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["grade"] = grade
        map["type"] = type
        map["subType"] = subType
        map["supply"]=supply
        presenter.getBooks(map)
    }

}
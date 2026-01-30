package com.bll.lnkwrite.ui.fragment

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.ItemSelectorDialog
import com.bll.lnkwrite.dialog.LongClickManageDialog
import com.bll.lnkwrite.manager.TextbookGreenDaoManager
import com.bll.lnkwrite.mvp.model.ItemList
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.mvp.model.book.TextbookBean
import com.bll.lnkwrite.mvp.presenter.MyHomeworkPresenter
import com.bll.lnkwrite.mvp.view.IContractView.IMyHomeworkView
import com.bll.lnkwrite.ui.activity.book.BookStoreTypeActivity
import com.bll.lnkwrite.ui.activity.book.TextBookStoreActivity
import com.bll.lnkwrite.ui.adapter.TextBookAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.utils.cloudManager.TextBookCloudUploadManager
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.common_fragment_title.tv_btn
import kotlinx.android.synthetic.main.fragment_list_tab.rv_list

class TextbookFragment : BaseFragment(), IMyHomeworkView {

    private val presenter = MyHomeworkPresenter(this)
    private var mAdapter: TextBookAdapter? = null
    private var textbooks = mutableListOf<TextbookBean>()
    private var tabId = 0
    private var position = 0
    private var textTypes= mutableListOf<ItemTypeBean>()

    private val uploadManager by lazy {
        TextBookCloudUploadManager(this)
    }

    override fun onCreateSuccess() {
        showToast(1,"设置作业本成功")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        setTitle(R.string.teaching)
        pageSize = 12

        tv_btn?.apply {
            showView(tv_btn)
            text="教材列表"
            setOnClickListener {
                customStartActivity(Intent(requireActivity(), TextBookStoreActivity::class.java))
            }
        }

        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initTab() {
        textTypes=DataBeanManager.textBookTypes
        mTabTypeAdapter?.setNewData(textTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        tabId = position
        pageIndex = 1
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),15f),
            DP2PX.dip2px(requireActivity(),50f),
            DP2PX.dip2px(requireActivity(),15f),0)
        layoutParams.weight=1f
        rv_list?.layoutParams= layoutParams

        mAdapter = TextBookAdapter(R.layout.item_bookstore, null).apply {
            rv_list?.layoutManager = GridLayoutManager(activity,4)//创建布局管理
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(4,DP2PX.dip2px(requireActivity(),30f)))
            setOnItemClickListener { adapter, view, position ->
                val book = textbooks[position]
                MethodManager.gotoTextBookDetails(activity,book)
            }
           onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@TextbookFragment.position = position
                onLongClick(textbooks[position])
                true
            }
        }
    }

    //长按显示课本管理
    private fun onLongClick(book: TextbookBean) {
        val beans = mutableListOf<ItemList>()
        beans.add(ItemList().apply {
            name = getString(R.string.delete)
            resId = R.mipmap.icon_setting_delete
        })
//        if (tabId >1&&DataBeanManager.students.size>0) {
//            beans.add(ItemList().apply {
//                name = "设置作业"
//                resId = R.mipmap.icon_setting_set
//            })
//        }

        LongClickManageDialog(requireActivity(),1, book.bookName, beans).builder()
            .setOnDialogClickListener {
                if (it == 0) {
                    MethodManager.deleteTextbook(book)
                } else {
                    val students=DataBeanManager.students
                    if (students.size==1){
                        val map = HashMap<String, Any>()
                        map["name"] = book.bookName
                        map["type"] = 2
                        map["childId"] = students[0].accountId
                        map["bookId"] = book.bookId
                        map["imageUrl"] = book.imageUrl
                        map["subject"] = book.subject
                        presenter.createHomeworkType(map)
                    }
                    else{
                        val lists= mutableListOf<ItemList>()
                        for (item in students){
                            lists.add(ItemList(item.accountId,item.nickname))
                        }
                        ItemSelectorDialog(requireActivity(),getString(R.string.select_student),lists).builder().setOnDialogClickListener{pos->
                            val map = HashMap<String, Any>()
                            map["name"] = book.bookName
                            map["type"] = 2
                            map["childId"] = students[pos].accountId
                            map["bookId"] = book.bookId
                            map["imageUrl"] = book.imageUrl
                            map["subject"] = book.subject
                            presenter.createHomeworkType(map)
                        }
                    }
                }
            }
    }

    override fun fetchData() {
        textbooks = TextbookGreenDaoManager.getInstance().queryAllTextBook(tabId, pageIndex, pageSize)
        val total = TextbookGreenDaoManager.getInstance().queryAllTextBook(tabId)
        setPageNumber(total.size)
        mAdapter?.setNewData(textbooks)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.TEXT_BOOK_EVENT->{
                fetchData()
            }
        }
    }

    /**
     * 上传两个月未使用书籍
     */
    fun upload(token: String){
        uploadManager.upload(token)
    }

}
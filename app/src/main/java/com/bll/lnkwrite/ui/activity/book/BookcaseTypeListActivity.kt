package com.bll.lnkwrite.ui.activity.book

import PopupClick
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants.BOOK_EVENT
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.dialog.BookcaseDetailsDialog
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.dialog.InputContentDialog
import com.bll.lnkwrite.dialog.ItemSelectorDialog
import com.bll.lnkwrite.dialog.LongClickManageDialog
import com.bll.lnkwrite.manager.BookDaoManager
import com.bll.lnkwrite.manager.ItemTypeDaoManager
import com.bll.lnkwrite.mvp.model.book.Book
import com.bll.lnkwrite.mvp.model.ItemList
import com.bll.lnkwrite.mvp.model.ItemTypeBean
import com.bll.lnkwrite.mvp.model.PopupBean
import com.bll.lnkwrite.ui.adapter.BookAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_list_tab.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * 书架分类
 */
class BookcaseTypeListActivity : BaseActivity() {
    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()
    private var tabPos = 0
    private var pos = 0 //当前书籍位置
    private val mBookDaoManager = BookDaoManager.getInstance()
    private var popupBeans = mutableListOf<PopupBean>()
    private var longBeans = mutableListOf<ItemList>()

    override fun layoutId(): Int {
        return R.layout.ac_list_tab
    }

    override fun initData() {
        pageSize = 12
        popupBeans.add(PopupBean(0, getString(R.string.type_create_str), false))
        popupBeans.add(PopupBean(1, getString(R.string.type_delete_str), false))
        popupBeans.add(PopupBean(2, getString(R.string.book_details_str), false))
    }

    override fun initView() {
        setPageTitle(R.string.type_list_str)
        showView(iv_manager)

        iv_manager?.setOnClickListener {
            PopupClick(this, popupBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        InputContentDialog(this, getString(R.string.type_create_str)).builder().setOnDialogClickListener {
                            if (ItemTypeDaoManager.getInstance().isExist(it, 2)) {
                                showToast(R.string.existed)
                                return@setOnDialogClickListener
                            }
                            val bookTypeBean = ItemTypeBean()
                            bookTypeBean.type = 2
                            bookTypeBean.date = System.currentTimeMillis()
                            bookTypeBean.title = it
                            ItemTypeDaoManager.getInstance().insertOrReplace(bookTypeBean)
                            mTabTypeAdapter?.addData(bookTypeBean)
                        }
                    }
                    1 -> {
                        if (tabPos==0){
                            showToast(R.string.toast_type_default_no_delete)
                            return@setOnSelectListener
                        }
                        val books = mBookDaoManager.queryAllBook(getTypeStr())
                        if (books.isNotEmpty()){
                            showToast(R.string.toast_type_exist_no_delete)
                            return@setOnSelectListener
                        }
                        CommonDialog(this).setContent(R.string.tips_is_delete).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun ok() {
                                ItemTypeDaoManager.getInstance().deleteBean(itemTabTypes[tabPos])
                                mTabTypeAdapter?.remove(tabPos)
                                tabPos=0
                                itemTabTypes[0].isCheck=true
                                pageIndex=1
                                fetchData()
                            }
                        })
                    }
                    2->{
                        BookcaseDetailsDialog(this).builder()
                    }
                }
            }
        }

        initRecycleView()
        initTab()
    }

    private fun initTab() {
        pageIndex=1
        itemTabTypes = ItemTypeDaoManager.getInstance().queryAll(2)
        itemTabTypes.add(0,ItemTypeBean().apply {
            title = getString(R.string.all)
            isCheck=true
        })
        itemTabTypes=MethodManager.setItemTypeBeanCheck(itemTabTypes,tabPos)
        mTabTypeAdapter?.setNewData(itemTabTypes)
        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        tabPos=position
        pageIndex = 1
        fetchData()
    }

    private fun initRecycleView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this, 30f), DP2PX.dip2px(this, 20f),
            DP2PX.dip2px(this, 30f), 0
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val bookBean = books[position]
                MethodManager.gotoBookDetails(this@BookcaseTypeListActivity, 1, bookBean)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                pos = position
                onLongClick()
                true
            }
        }
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, DP2PX.dip2px(this, 25f)))
    }

    //删除书架书籍
    private fun onLongClick() {
        val book = books[pos]
        longBeans.clear()
        longBeans.add(ItemList().apply {
            name = getString(R.string.delete)
            resId = R.mipmap.icon_setting_delete
        })
        if (tabPos==0) {
            longBeans.add(ItemList().apply {
                name = getString(R.string.set)
                resId = R.mipmap.icon_setting_set
            })
        } else {
            longBeans.add(ItemList().apply {
                name = getString(R.string.shift_out)
                resId = R.mipmap.icon_setting_out
            })
        }

        LongClickManageDialog(this, book.bookName, longBeans).builder()
            .setOnDialogClickListener {
                if (it == 0) {
                    mAdapter?.remove(pos)
                    MethodManager.deleteBook(book)
                    fetchData()
                } else {
                    if (tabPos==0) {
                        val types = ItemTypeDaoManager.getInstance().queryAll(2)
                        val lists = mutableListOf<ItemList>()
                        for (i in types.indices) {
                            lists.add(ItemList(i, types[i].title))
                        }
                        ItemSelectorDialog(this, getString(R.string.type_set_str), lists).builder().setOnDialogClickListener {
                            val typeStr = types[it].title
                            book.subtypeStr = typeStr
                            mBookDaoManager.insertOrReplaceBook(book)
                            fetchData()
                        }
                    } else {
                        book.subtypeStr = ""
                        mBookDaoManager.insertOrReplaceBook(book)
                        fetchData()
                    }
                }
            }
    }

    private fun getTypeStr():String{
        return if (tabPos==0) "" else itemTabTypes[tabPos].title
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == BOOK_EVENT) {
            fetchData()
        }
    }

    override fun fetchData() {
        books = mBookDaoManager.queryAllBook(getTypeStr(), pageIndex, pageSize)
        val total = mBookDaoManager.queryAllBook(getTypeStr())
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }

}
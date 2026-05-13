package com.bll.lnkwrite.ui.fragment

import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.Constants.BOOK_EVENT
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.dialog.CommonDialog
import com.bll.lnkwrite.manager.BookDaoManager
import com.bll.lnkwrite.mvp.model.book.Book
import com.bll.lnkwrite.ui.activity.book.BookStoreTypeActivity
import com.bll.lnkwrite.ui.adapter.BookAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.common_fragment_title.tv_btn
import kotlinx.android.synthetic.main.fragment_list_content_tab.rv_list
import kotlinx.android.synthetic.main.fragment_list_tab.rv_tab

class BookFragment:BaseFragment() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        setTitle(R.string.bookcase)
        disMissView(rv_tab)
        pageSize=12

        tv_btn?.apply {
            showView(tv_btn)
            setText(R.string.bookstore)
            setOnClickListener {
                customStartActivity(Intent(requireActivity(), BookStoreTypeActivity::class.java))
            }
        }
        initRecyclerView()
    }

    private fun initRecyclerView(){
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(), 15f), DP2PX.dip2px(requireActivity(), 80f),
            DP2PX.dip2px(requireActivity(), 15f), 0
        )
        layoutParams.weight = 1f
        rv_list?.layoutParams = layoutParams

        rv_list?.addItemDecoration(SpaceGridItemDeco(4, DP2PX.dip2px(requireActivity(), 40f)))
        rv_list?.layoutManager = GridLayoutManager(requireActivity(), 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_bookstore, null).apply {
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                MethodManager.gotoBookDetails(requireActivity(),1, books[position])
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                CommonDialog(requireActivity(),1).setContent(R.string.tips_is_delete).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                    override fun ok() {
                        MethodManager.deleteBook(books[position])
                    }
                })
                true
            }
        }
    }

    override fun lazyLoad() {
        fetchData()
    }

    override fun fetchData() {
        val bookDaoManager= BookDaoManager.getInstance()
        books = bookDaoManager.queryAllBook(pageIndex, pageSize)
        val total = bookDaoManager.queryAllBook().size
        setPageNumber(total)
        mAdapter?.setNewData(books)
    }


    override fun onRefreshData() {
        fetchData()
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            BOOK_EVENT -> {
                fetchData()
            }
        }
    }
}
package com.bll.lnkwrite.ui.fragment.book

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.MethodManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.manager.BookDaoManager
import com.bll.lnkwrite.mvp.model.book.Book
import com.bll.lnkwrite.ui.adapter.BookAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.rv_list

class BookReadingFragment: BaseFragment() {

    private var mAdapter: BookAdapter?=null
    private var books= mutableListOf<Book>()//所有数据

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),15f),
            DP2PX.dip2px(requireActivity(),80f),
            DP2PX.dip2px(requireActivity(),15f),0)
        layoutParams.weight=1f
        rv_list?.layoutParams= layoutParams

        mAdapter = BookAdapter(R.layout.item_bookstore ,null).apply {
            rv_list?.layoutManager = GridLayoutManager(activity,4)//创建布局管理
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(4, 60))
            setOnItemClickListener { adapter, view, position ->
                val bookBean=books[position]
                MethodManager.gotoBookDetails(requireActivity(),1,bookBean)
            }
        }
    }


    override fun fetchData() {
        books= BookDaoManager.getInstance().queryAllBook(true,12)
        mAdapter?.setNewData(books)
    }

    override fun onRefreshData() {
        fetchData()
    }

}
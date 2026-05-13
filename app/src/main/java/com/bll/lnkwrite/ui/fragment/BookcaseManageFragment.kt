package com.bll.lnkwrite.ui.fragment

import android.content.Intent
import com.bll.lnkwrite.Constants.BOOK_EVENT
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseFragment
import com.bll.lnkwrite.ui.activity.book.BookStoreTypeActivity
import com.bll.lnkwrite.ui.fragment.book.BookCaseFragment
import com.bll.lnkwrite.ui.fragment.book.BookReadingFragment
import com.bll.lnkwrite.utils.cloudManager.BookCloudUploadManager
import com.bll.lnkwrite.widget.TabRadioGroup
import kotlinx.android.synthetic.main.common_fragment_title.tv_btn
import kotlinx.android.synthetic.main.fragment_bookcase_manage.tabRadioGroup

class BookcaseManageFragment: BaseFragment() {

    private var bookReadingFragment: BookReadingFragment?=null
    private var bookCaseFragment: BookCaseFragment?=null

    private val uploadManager by lazy {
        BookCloudUploadManager(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase_manage
    }

    override fun initView() {
       val tabTitles = arrayListOf(getString(R.string.reading), getString(R.string.bookcase))

        tv_btn?.apply {
            showView(tv_btn)
            setText(R.string.bookstore)
            setOnClickListener {
                customStartActivity(Intent(requireActivity(), BookStoreTypeActivity::class.java))
            }
        }

        bookCaseFragment= BookCaseFragment()
        bookReadingFragment= BookReadingFragment()

        switchFragment(2,lastFragment,bookReadingFragment)

        tabRadioGroup.setTabTitles(tabTitles)
        tabRadioGroup.setOnTabSelectedListener(object : TabRadioGroup.OnTabSelectedListener {
            override fun onTabSelected(position: Int, title: String) {
                when(position){
                    0->{
                        switchFragment(2,lastFragment,bookReadingFragment)
                    }
                    1->{
                        switchFragment(2,lastFragment,bookCaseFragment)
                    }
                }
            }
        })
    }

    override fun lazyLoad() {
    }

    override fun onRefreshData() {
        bookCaseFragment?.onRefreshData()
        bookReadingFragment?.onRefreshData()
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            BOOK_EVENT -> {
                onRefreshData()
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
package com.bll.lnkwrite.ui.activity

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.ui.adapter.MainListAdapter
import com.bll.lnkwrite.ui.fragment.cloud.*
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_cloud_storage.*

class CloudStorageActivity:BaseActivity() {
    private var lastPosition = 0
    private var mHomeAdapter: MainListAdapter? = null
    private var lastFragment: Fragment? = null

    private var bookcaseFragment: CloudBookcaseFragment? = null
    private var textbookFragment: CloudTextbookFragment? = null
    private var noteFragment: CloudNoteFragment? = null
    private var diaryFragment: CloudDiaryFragment? = null
    private var screenshotFragment: CloudScreenshotFragment? = null

    override fun layoutId(): Int {
        return R.layout.ac_cloud_storage
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle(R.string.cloud_storage)

        bookcaseFragment = CloudBookcaseFragment()
        textbookFragment= CloudTextbookFragment()
        noteFragment= CloudNoteFragment()
        diaryFragment = CloudDiaryFragment()
        screenshotFragment = CloudScreenshotFragment()

        switchFragment(lastFragment, bookcaseFragment)

        mHomeAdapter = MainListAdapter(R.layout.item_main_list, DataBeanManager.getIndexDataCloud()).apply {
            rv_list.layoutManager = LinearLayoutManager(this@CloudStorageActivity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                updateItem(lastPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchFragment(lastFragment, bookcaseFragment)//书架
                    1 -> switchFragment(lastFragment, textbookFragment)//课本
                    2 -> switchFragment(lastFragment, noteFragment)//笔记
                    3 -> switchFragment(lastFragment, diaryFragment)//日记
                    4 -> switchFragment(lastFragment, screenshotFragment)//截图
                }
                lastPosition=position
            }
        }
    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.frame_layout, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }
}
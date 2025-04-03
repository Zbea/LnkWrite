package com.bll.lnkwrite.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseActivity
import com.bll.lnkwrite.mvp.model.teaching.ExamRankList
import com.bll.lnkwrite.mvp.model.teaching.Score
import com.bll.lnkwrite.mvp.presenter.ScoreRankPresenter
import com.bll.lnkwrite.mvp.view.IContractView
import com.bll.lnkwrite.ui.adapter.ScoreAdapter
import com.bll.lnkwrite.utils.DP2PX
import com.bll.lnkwrite.widget.SpaceGridItemDecoScore
import kotlinx.android.synthetic.main.ac_score.*

class ScoreActivity:BaseActivity(),IContractView.IScoreRankView{

    private var type=0
    private val mPresenter= ScoreRankPresenter(this)
    private var mAdapter: ScoreAdapter?=null
    private var scores= mutableListOf<Score>()

    override fun onScore(scores: MutableList<Score>) {
        this.scores=scores
        mAdapter?.setNewData(scores)
    }

    override fun onExamScore(list: ExamRankList) {
        for (item in list.list){
            scores.add(Score().apply {
                classId=item.classId
                className=item.className
                score=item.score
                name=item.studentName
            })
        }
        mAdapter?.setNewData(scores)
    }

    override fun layoutId(): Int {
        return R.layout.ac_score
    }

    override fun initData() {
        type=intent.flags
        val id=intent.getIntExtra("id",0)
        if (type==0){
            mPresenter.onExamScore(id)
        }
        else{
            mPresenter.onScore(id)
        }
    }

    override fun initView() {
        setPageTitle(R.string.score_ranking)

        iv_arrow_page_up.setOnClickListener {
            rv_list.scrollBy(0,-DP2PX.dip2px(this,100f))
        }

        iv_arrow_page_down.setOnClickListener {
            rv_list.scrollBy(0, DP2PX.dip2px(this,100f))
        }

        initRecyclerView()

    }

    private fun initRecyclerView(){
        mAdapter = ScoreAdapter(R.layout.item_score,null)
        rv_list.layoutManager = GridLayoutManager(this,2)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDecoScore(DP2PX.dip2px(this,40f),0))
    }

}
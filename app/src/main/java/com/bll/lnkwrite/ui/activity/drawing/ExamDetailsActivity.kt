package com.bll.lnkwrite.ui.activity.drawing

import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseDrawingActivity
import com.bll.lnkwrite.mvp.model.teaching.ExamList
import com.bll.lnkwrite.mvp.model.teaching.ExamScoreItem
import com.bll.lnkwrite.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_tool.*

class ExamDetailsActivity:BaseDrawingActivity() {

    private var examBean: ExamList.ExamBean?=null
    private var images= mutableListOf<String>()
    private var posImage=0

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        examBean=intent.getBundleExtra("bundle")?.getSerializable("examBean") as ExamList.ExamBean
        images= examBean?.teacherUrl?.split(",") as MutableList<String>
        scoreMode=examBean?.questionMode!!
        correctMode=examBean?.questionType!!
        if (examBean?.question?.isNotEmpty() == true)
            currentScores= scoreJsonToList(examBean?.question!!) as MutableList<ExamScoreItem>
        if (examBean?.answerUrl?.isNotEmpty()==true)
            answerImages= examBean?.answerUrl!!.split(",") as MutableList<String>
    }

    override fun initView() {
        disMissView(iv_btn,iv_tool,iv_catalog,iv_expand)
        setViewElikUnable(iv_score,ll_score)
        setDisableTouchInput(true)
        showView(iv_score)
        if (correctMode<3){
            showView(rv_list_score)
            disMissView(rv_list_multi)
        }
        else{
            showView(rv_list_multi)
            disMissView(rv_list_score)
        }

        if (answerImages.size>0){
            showView(tv_answer)
        }
        else{
            disMissView(tv_answer)
        }

        tv_correct_title.text=examBean?.examName
        tv_total_score.text=examBean?.score

        setContentImage()
        initScoreView()
    }


    override fun onPageDown() {
        if (posImage< images.size-1){
            posImage+=1
            setContentImage()
        }
    }

    override fun onPageUp() {
        if (posImage>0){
            posImage-=1
            setContentImage()
        }
    }

    /**
     * 设置学生提交图片展示
     */
    private fun setContentImage(){
        tv_page.text="${posImage+1}"
        tv_page_total.text="${images.size}"
        GlideUtils.setImageCacheUrl(this, images[posImage],v_content_b)
    }

}
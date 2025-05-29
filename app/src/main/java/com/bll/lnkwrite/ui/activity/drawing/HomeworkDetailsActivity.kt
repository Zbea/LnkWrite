package com.bll.lnkwrite.ui.activity.drawing

import com.bll.lnkwrite.DataBeanManager
import com.bll.lnkwrite.R
import com.bll.lnkwrite.base.BaseDrawingActivity
import com.bll.lnkwrite.dialog.ResultStandardDetailsDialog
import com.bll.lnkwrite.dialog.ScoreDetailsDialog
import com.bll.lnkwrite.mvp.model.teaching.ScoreItem
import com.bll.lnkwrite.mvp.model.TeacherHomeworkList
import com.bll.lnkwrite.mvp.model.teaching.ResultStandardItem
import com.bll.lnkwrite.utils.GlideUtils
import com.bll.lnkwrite.utils.ScoreItemUtils
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_tool.*

class HomeworkDetailsActivity:BaseDrawingActivity() {

    private var homeworkBean:TeacherHomeworkList.TeacherHomeworkBean?=null
    private var images= mutableListOf<String>()
    private var posImage=0

    val items= mutableListOf<ResultStandardItem>()

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        homeworkBean=intent.getBundleExtra("bundle")?.getSerializable("homeworkBean") as TeacherHomeworkList.TeacherHomeworkBean
        when (homeworkBean?.status!!){
            1->{
                images= homeworkBean?.homeworkContent?.split(",") as MutableList<String>
            }
            2->{
                images= homeworkBean?.submitContent?.split(",") as MutableList<String>
            }
            3->{
                images= homeworkBean?.correctContent?.split(",") as MutableList<String>
            }
        }

        if (homeworkBean?.type==1){
            when(homeworkBean?.subType){
                3->{
                    DataBeanManager.getResultStandardItem3s()
                }
                6->{
                    DataBeanManager.getResultStandardItem6s()
                }
                8->{
                    DataBeanManager.getResultStandardItem8s()
                }
                else->{
                    if (homeworkBean?.typeName=="作文作业本"){
                        DataBeanManager.getResultStandardItem2s()
                    }
                    else{
                        DataBeanManager.getResultStandardItems()
                    }
                }
            }
        }

    }

    override fun initView() {
        disMissView(iv_btn,iv_tool,iv_catalog,iv_expand)
        setDisableTouchInput(true)

        if (homeworkBean?.status==3)
            showView(iv_score)

        iv_score.setOnClickListener {
            if (homeworkBean?.type==1&&homeworkBean?.subType!=1){
                ResultStandardDetailsDialog(this,homeworkBean?.title!!,homeworkBean?.score!!.toDouble(),homeworkBean?.question!!,items).builder()
            }
            else{
                val answerImages= homeworkBean?.answerUrl!!.split(",") as MutableList<String>
                ScoreDetailsDialog(this,homeworkBean!!.title,homeworkBean!!.score.toDouble(),homeworkBean?.questionType!!,homeworkBean?.questionMode!!,answerImages,homeworkBean!!.question).builder()
            }
        }

        setContentImage()
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
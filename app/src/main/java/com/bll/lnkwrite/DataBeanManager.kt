package com.bll.lnkwrite

import com.bll.lnkwrite.MyApplication.Companion.mContext
import com.bll.lnkwrite.mvp.model.*
import com.bll.lnkwrite.mvp.model.catalog.CatalogChildBean
import com.bll.lnkwrite.mvp.model.catalog.CatalogParentBean
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.util.*

object DataBeanManager {

    var grades= mutableListOf<ItemList>()
    var typeGrades= mutableListOf<ItemList>()
    var courses= mutableListOf<ItemList>()
    var versions= mutableListOf<ItemList>()
    var students= mutableListOf<StudentBean>()

    val homeworkType = arrayOf(mContext.getString(R.string.teacher_homework_str),mContext.getString(R.string.classGroup_exam_str),mContext.getString(R.string.school_exam_str)
        ,mContext.getString(R.string.my_homework),mContext.getString(R.string.my_homework_correct))

    var resources = arrayOf(mContext.getString(R.string.app_news_str),mContext.getString(R.string.app_book_str),mContext.getString(R.string.app_journal_str)
        ,mContext.getString(R.string.app_tool_str),mContext.getString(R.string.wallpaper_str),mContext.getString(R.string.calender_str))

    val popupGrades: MutableList<PopupBean>
        get() {
            val list= mutableListOf<PopupBean>()
            for (i in grades.indices){
                list.add(PopupBean(grades[i].type, grades[i].desc, i == 0))
            }
            return list
        }

    val popupTypeGrades: MutableList<PopupBean>
        get() {
            val list= mutableListOf<PopupBean>()
            for (i in typeGrades.indices){
                list.add(PopupBean(typeGrades[i].type, typeGrades[i].desc, i == 0))
            }
            return list
        }

    val popupCourses: MutableList<PopupBean>
        get() {
            val list= mutableListOf<PopupBean>()
            for (i in courses.indices){
                list.add(PopupBean(courses[i].type, courses[i].desc, false))
            }
            return list
        }

    fun popupCourses(type:Int): MutableList<PopupBean>{
        val list= mutableListOf<PopupBean>()
        for (i in courses.indices){
            list.add(PopupBean(courses[i].type, courses[i].desc, courses[i].type==type))
        }
        return list
    }

    val popupStudents: MutableList<PopupBean>
        get() {
            val list= mutableListOf<PopupBean>()
            for (i in students.indices) {
                list.add(PopupBean(students[i].accountId, students[i].nickname, i == 0))
            }
            return list
        }

    /**
     * 获取index栏目
     */
    fun getIndexDataCloud(): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()

        val h1 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_bookcase)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_bookcase_check)
            isCheck = true
            name = mContext.getString(R.string.main_bookcase_title)
        }

        val h2 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_textbook)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_textbook_check)
            name = mContext.getString(R.string.main_teaching_title)
        }

        val h3 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_note)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_note_check)
            name = mContext.getString(R.string.main_note_title)
        }

        val h4 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_diary)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_diary_check)
            name = mContext.getString(R.string.main_diary_title)
        }

        val h5 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_screenshot)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_screenshot_check)
            name = mContext.getString(R.string.main_screenshot_title)
        }
        list.add(h1)
        list.add(h2)
        list.add(h3)
        list.add(h4)
        list.add(h5)
        return list
    }


    /**
     * 获取index栏目
     */
    fun getIndexDataLeft(isBing:Boolean): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()
        val h0 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_home)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_home_check)
            isCheck = true
            name = mContext.getString(R.string.main_title)
        }

        val h1 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_bookcase)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_bookcase_check)
            name = mContext.getString(R.string.main_bookcase_title)
        }

        val h2 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_document)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_document_check)
            name = mContext.getString(R.string.main_document_title)
        }

        val h3 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_app)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_app_check)
            name =  mContext.getString(R.string.main_app_title)
        }
        val h4 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_textbook)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_textbook_check)
            name = mContext.getString(R.string.main_teaching_title)
        }
        list.add(h0)
        list.add(h1)
        list.add(h2)
        list.add(h3)
        if (isBing)
            list.add(h4)
        return list
    }

    /**
     * 获取index栏目
     */
    fun getIndexDataRight(isBing:Boolean): MutableList<ItemList> {
        val list = mutableListOf<ItemList>()
        val h0 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_home)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_home_check)
            isCheck = true
            name = mContext.getString(R.string.main_title)
        }

        val h1 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_note)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_note_check)
            name = mContext.getString(R.string.main_note_title)
        }

        val h2 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_painting)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_painting_check)
            name = mContext.getString(R.string.main_painting_title)
        }

        val h3 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_screenshot)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_screenshot_check)
            name =  mContext.getString(R.string.main_screenshot_title)
        }
        val h4 = ItemList().apply {
            icon = mContext.getDrawable(R.mipmap.icon_tab_homework)
            icon_check = mContext.getDrawable(R.mipmap.icon_tab_homework_check)
            name = mContext.getString(R.string.main_homework_title)
        }
        list.add(h0)
        list.add(h1)
        list.add(h2)
        list.add(h3)
        if (isBing)
            list.add(h4)
        return list
    }

    val textBookTypes: MutableList<ItemTypeBean>
        get() {
            val list= mutableListOf<ItemTypeBean>()
            list.add(ItemTypeBean().apply {
                title = mContext.getString(R.string.teaching_textbook_str)
                isCheck=true
            })
            list.add(ItemTypeBean().apply {
                title =  mContext.getString(R.string.teaching_textbook_other)
                isCheck=false
            })
            list.add(ItemTypeBean().apply {
                title =  mContext.getString(R.string.teaching_homework_book_str)
                isCheck=false
            })
            list.add(ItemTypeBean().apply {
                title = mContext.getString(R.string.teaching_homework_book_other)
                isCheck=false
            })
            return list
        }

    //日记内容选择
    val diaryModules: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_diary_details_bg_1
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_fgb)
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_diary_details_bg_2
            })
            return list
        }

    val freenoteModules: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_kbb)
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_freenote_bg_1
            })
            return list
        }

    //笔记本内容选择
    val noteModules: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_kbb)
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_1
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_fgb)
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_note_details_bg_2
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_yyb)
                resId = R.mipmap.icon_note_module_bg_3
                resContentId = R.mipmap.icon_note_details_bg_3
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_tzb)
                resId = R.mipmap.icon_note_module_bg_4
                resContentId = R.mipmap.icon_note_details_bg_4
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_wxp)
                resId = R.mipmap.icon_note_module_bg_5
                resContentId = R.mipmap.icon_note_details_bg_5
            })
            return list
        }

    //学期选择
    val popupSemesters: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            list.add(PopupBean(1, mContext.getString(R.string.semester_last),true))
            list.add(PopupBean(2,mContext.getString(R.string.semester_next),false))
            return list
        }

    val popupSupplys: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            list.add(PopupBean(1, mContext.getString(R.string.official_str),true))
            list.add(PopupBean(2,mContext.getString(R.string.thirdParty_str),false))
            return list
        }

    val bookStoreTypes: MutableList<ItemList>
        get() {
            val list = mutableListOf<ItemList>()
            list.add(ItemList(1, mContext.getString(R.string.book_tab_gj)))
            list.add(ItemList(2, mContext.getString(R.string.book_tab_zrkx)))
            list.add(ItemList(3, mContext.getString(R.string.book_tab_shkx)))
            list.add(ItemList(4, mContext.getString(R.string.book_tab_sxkx)))
            list.add(ItemList(5, mContext.getString(R.string.book_tab_yscn)))
            list.add(ItemList(6, mContext.getString(R.string.book_tab_ydjk)))
            return list
        }

    val weeks: MutableList<DateWeek>
        get() {
            val list= mutableListOf<DateWeek>()
            list.add(
                DateWeek(mContext.getString(R.string.week_1),  2, false)
            )
            list.add(
                DateWeek(mContext.getString(R.string.week_2),  3, false)
            )
            list.add(
                DateWeek(mContext.getString(R.string.week_3),  4, false)
            )
            list.add(
                DateWeek(mContext.getString(R.string.week_4),  5, false)
            )
            list.add(
                DateWeek(mContext.getString(R.string.week_5),  6, false)
            )
            list.add(
                DateWeek(mContext.getString(R.string.week_6),  7, false)
            )
            list.add(
                DateWeek(mContext.getString(R.string.week_7),  8, false)
            )
            return list
        }

    fun getWeekStr(week:Int):String{
        var weekStr=""
        for (item in weeks){
            if (item.week==week)
                weekStr=item.name
        }
        return weekStr
    }

    fun getCourseId(courseStr:String):Int{
        var courseId=0
        for (item in courses){
            if (item.desc==courseStr)
                courseId=item.type
        }
        return courseId
    }

    fun getCourseStr(courseId:Int):String{
        var courseStr=""
        for (item in courses){
            if (item.type==courseId)
                courseStr=item.desc
        }
        return courseStr
    }

    fun getBookVersionStr(version: Int): String {
        var cls=""
        for (item in versions) {
            if (item.type == version){
                cls=item.desc
            }
        }
        return cls
    }

    fun operatingGuideInfo():List<MultiItemEntity>{
        val list= mutableListOf<MultiItemEntity>()
        val types= mutableListOf("一、"+ mContext.getString(R.string.instruction_main),"二、"+mContext.getString(R.string.instruction_manager),"三、"+mContext.getString(R.string.instruction_tool))
        val mainStrs= mutableListOf(mContext.getString(R.string.register),mContext.getString(R.string.key_interface),mContext.getString(R.string.status_bar),mContext.getString(R.string.home)
            ,mContext.getString(R.string.bookcase),mContext.getString(R.string.note),mContext.getString(R.string.app),mContext.getString(R.string.free_note),mContext.getString(R.string.diary)
            ,mContext.getString(R.string.project),mContext.getString(R.string.screenshot),mContext.getString(R.string.message))
        val managerStrs= mutableListOf(mContext.getString(R.string.instruction_manager))
        val toolStrs= mutableListOf(mContext.getString(R.string.Toolkit),mContext.getString(R.string.calender),mContext.getString(R.string.screenshot),mContext.getString(R.string.geometry_title_str))
        val childTypes= mutableListOf(mainStrs,managerStrs,toolStrs)
        for (type in types){
            val index=types.indexOf(type)
            val catalogParentBean = CatalogParentBean()
            catalogParentBean.title=type
            for (childType in childTypes[index]){
                val catalogChildBean = CatalogChildBean()
                catalogChildBean.title = childType
                catalogChildBean.parentPosition=index
                catalogChildBean.pageNumber = childTypes[index].indexOf(childType)+1
                catalogParentBean.addSubItem(catalogChildBean)
            }
            list.add(catalogParentBean)
        }
        return list
    }

}
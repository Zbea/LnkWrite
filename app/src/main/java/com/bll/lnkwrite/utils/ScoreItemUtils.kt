package com.bll.lnkwrite.utils

import com.bll.lnkwrite.mvp.model.teaching.ScoreItem
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.util.regex.Pattern

object ScoreItemUtils {

    fun getAIJsonScore(message: String): String {
        val pattern = Pattern.compile("```json\\n(.*?)\\n```", Pattern.DOTALL)
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return ""
    }

    fun updateAIJsonScores(currentScores: List<ScoreItem>, updateList: List<ScoreItem>) {
        var i = 0
        // 递归更新函数
        fun updateNode(currentItem:  ScoreItem,parentItem: ScoreItem?) {
            if (i >= updateList.size) return
            // 优先检查当前节点是否匹配
            if (currentItem.label == updateList[i].label) {
                currentItem.score = updateList[i].score
                i += 1
                //将子节点值赋值给父节点
                if (parentItem!=null){
                    parentItem.score+=currentItem.score
                }
                return  // 匹配成功后终止当前分支的进一步检查
            }
            // 递归处理子节点
            currentItem.childScores?.forEach { child ->
                updateNode(child,currentItem)
            }
        }

        // 遍历根节点
        currentScores.forEach { rootNode ->
            updateNode(rootNode,null)
        }
    }

    /**
     * 格式序列化  题目分数转行list集合
     */
    fun questionToList(json: String,correctModule: Int): MutableList<ScoreItem> {
        if (json.isEmpty()){
            return mutableListOf()
        }
        val list: MutableList<ScoreItem> = try {
            Gson().fromJson(json, object : TypeToken<MutableList<ScoreItem>>() {}.type) as MutableList<ScoreItem>
        } catch (e:Exception){
            mutableListOf()
        }

        setInitListScore(list)
        jsonListToModuleList(list, correctModule)

//        //定义排除策略：忽略 ScoreItem 类的 parentItem 字段
//        val exclusionStrategy: ExclusionStrategy = object : ExclusionStrategy {
//            override fun shouldSkipField(f: FieldAttributes): Boolean {
//                // 条件：字段名是 "parentItem" 且所属类是 ScoreItem
//                return "parentItem" == f.name && ScoreItem::class.java == f.declaringClass
//            }
//
//            override fun shouldSkipClass(clazz: Class<*>?): Boolean {
//                return false // 不忽略任何类
//            }
//        }
//        val gson = GsonBuilder()
//            .setExclusionStrategies(exclusionStrategy) // 应用自定义排除规则
//            .setPrettyPrinting() // 格式化输出（可选）
//            .create()
//        Log.d(Constants.DEBUG,gson.toJson(list[0]))

        return list
    }


    fun listToJson(scoreItems:MutableList<ScoreItem>):String{
        val gson = GsonBuilder()
            .registerTypeAdapter(ScoreItem::class.java, ScoreItemTypeAdapter())
            .create()
        return gson.toJson(scoreItems)
    }

    private class ScoreItemTypeAdapter : TypeAdapter<ScoreItem?>() {
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: ScoreItem?) {
            if (value == null) {
                out.nullValue()
                return
            }
            out.beginObject()

            // 只写入需要的核心字段（过滤 level/parentItem/sortStr 等）
            out.name("score").value(value.score)
            out.name("type").value(value.type)
            out.name("label").value(value.label)
            out.name("pos").value(value.pos)

            // 处理嵌套的 childScores 列表（递归序列化，核心逻辑正确）
            out.name("childScores")
            out.beginArray() // 标记 JSON 数组开始
            value.childScores?.takeIf { it.isNotEmpty() }?.forEach { child ->
                this.write(out, child) // 递归序列化子项，语法正确
            }
            out.endArray() // 标记 JSON 数组结束（修正注释，补全语法）

            out.endObject()
        }

        @Throws(IOException::class)
        override fun read(`in`: JsonReader?): ScoreItem? {
            // 若后续需要反序列化，可补充以下逻辑（当前只序列化，返回 null 也可）
            return null
        }
    }

    /**
     * 题目分数多级树列表转成模板级数
     */
    private fun jsonListToModuleList(list: List<ScoreItem>,correctModule: Int){
        when (correctModule) {
            1,2 -> {
                list.forEachIndexed { index, scoreItem ->
                    val sort=index+1
                    scoreItem.sortStr=if (correctModule==1) ChineseNumberConverter.toMixedChineseNumber(sort)  else "$sort"
                }
            }
            5->{
                list.forEachIndexed { index, scoreItem ->
                    scoreItem.sortStr="${index+1}"
                    if (hasChild(scoreItem)){
                        scoreItem.childScores.forEachIndexed { index1, scoreItem1 ->
                            scoreItem1.parentItem=scoreItem
                            if (!scoreItem.isChildLevel)
                                scoreItem1.sortStr="(${index1+1})"

                            if (hasChild(scoreItem1)) {
                                scoreItem1.childScores.forEachIndexed { index2, scoreItem2 ->
                                    scoreItem2.parentItem=scoreItem1
                                    if (!scoreItem1.isChildLevel)
                                        scoreItem2.sortStr=ChineseNumberConverter.toCircleNumber(index2+1)
                                }
                            }
                        }
                    }
                }
            }
            3,6->{
                list.forEachIndexed { rootIndex, rootItem ->
                    rootItem.sortStr=ChineseNumberConverter.toMixedChineseNumber(rootIndex+1)
                    if (hasChild(rootItem)){
                        rootItem.childScores.forEachIndexed { index, scoreItem ->
                            scoreItem.parentItem=rootItem
                            if (!rootItem.isChildLevel)
                                scoreItem.sortStr="${index+1}"

                            if (hasChild(scoreItem)){
                                scoreItem.childScores.forEachIndexed { index1, scoreItem1 ->
                                    scoreItem1.parentItem=scoreItem
                                    if (!scoreItem.isChildLevel)
                                        scoreItem1.sortStr="(${index1+1})"

                                    if (hasChild(scoreItem1)) {
                                        scoreItem1.childScores.forEachIndexed { index2, scoreItem2 ->
                                            scoreItem2.parentItem=scoreItem1
                                            if (!scoreItem1.isChildLevel)
                                                scoreItem2.sortStr=ChineseNumberConverter.toCircleNumber(index2+1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            4,7->{
                var baseIndex=0
                list.forEachIndexed { rootIndex, rootItem ->
                    rootItem.sortStr=ChineseNumberConverter.toMixedChineseNumber(rootIndex+1)
                    if (hasChild(rootItem)){
                        rootItem.childScores.forEachIndexed { index, scoreItem ->
                            scoreItem.parentItem=rootItem
                            if (rootItem.isChildLevel)
                                scoreItem.sortStr="${baseIndex+index+1}"

                            if (hasChild(scoreItem)){
                                scoreItem.childScores.forEachIndexed { index1, scoreItem1 ->
                                    scoreItem1.parentItem=scoreItem
                                    if (scoreItem.isChildLevel)
                                        scoreItem1.sortStr="(${index1+1})"

                                    if (hasChild(scoreItem1)) {
                                        scoreItem1.childScores.forEachIndexed { index2, scoreItem2 ->
                                            scoreItem2.parentItem=scoreItem1
                                            if (scoreItem1.isChildLevel)
                                                scoreItem2.sortStr=ChineseNumberConverter.toCircleNumber(index2+1)
                                        }
                                    }
                                }
                            }
                        }
                        baseIndex+=rootItem.childScores.size
                    }
                }
            }
        }
    }

    private fun hasChild(item:ScoreItem):Boolean{
        return !item.childScores.isNullOrEmpty()
    }

    /**
     * 给数据节点赋分、以及统计对错
     */
    private fun setInitListScore(list:List<ScoreItem>){
        list.forEach { item ->
            // 处理子节点（递归：先确保子节点的result已计算，避免父节点依赖未初始化的子节点数据）
            if (!item.childScores.isNullOrEmpty()) {
                setInitListScore(item.childScores) // 递归处理子节点，保证子节点result/score/label已就绪
            }
            //父节点逻辑：仅当score/label为空时，才通过子节点汇总赋值
            if (item.childScores.isNullOrEmpty()) {
                return@forEach
            }
            // 父节点：判断score/label是否为空
            if (item.score == 0.0) {
                item.score = getItemScoreTotal(item.childScores)
            }
            if (item.label == 0.0) {
                item.label = getItemLabelTotal(item.childScores)
            }
        }
    }

    /**
     * 计算父节点总score（子节点score汇总）
     */
    fun getItemScoreTotal(list: MutableList<ScoreItem>): Double {
        return list.sumOf { it.score }
    }

    /**
     * 轮询赋值父节点总label
     */
    private fun getItemLabelTotal(list: MutableList<ScoreItem>): Double {
        return list.sumOf { it.label }
    }

}
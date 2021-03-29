package com.like360.demo.utils

import android.app.ActivityManager
import android.content.Context
import android.text.format.Formatter
import kotlin.math.roundToLong

/**
 * 说明：获取设备RAM内存信息
 * 文件名称：MemoryUtils
 * 创建者: hallo
 * 邮箱: hallo@xxx.xx
 * 时间: 2021/3/9 6:48 PM
 * 版本：V1.0.0
 */
object MemoryUtils {
    /***
     * 获取系统可用的内存信息
     */
    fun getAvailMem(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem
    }

    /***
     * 获取系统总的内存信息
     */
    fun getTotalMem(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem
    }


    fun getAvailMemWithUnit(context: Context): String {
        val size = getAvailMem(context)
        return Formatter.formatFileSize(context, size)

    }


    fun getTotalMemWithUnit(context: Context): String {
        val size = getTotalMem(context)
        return Formatter.formatFileSize(context, size)
    }

    /***
     * 获取已用内存占用的百分比
     */
    fun getUsedMemPercent(context: Context): Int {
        val totalSize = getTotalMem(context)
        val availSize = getAvailMem(context)
        val usedSize = totalSize- availSize
        return (usedSize*100f/totalSize).toInt()
    }
}
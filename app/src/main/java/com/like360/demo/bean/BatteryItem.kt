package com.like360.demo.bean

/**
 * 说明：电池相关信息item
 * 文件名称：BatteryItem
 * 创建者: hallo
 * 邮箱: hallo@xxx.xx
 * 时间: 2021/3/9 5:00 PM
 * 版本：V1.0.0
 */
data class BatteryItem(
    /***
     * 温度
     */
    val temp: String,
    /***
     * 电量百分比
     */
    val level: String,
    /***
     * 电池容量
     */
    val capacity: String,
    /***
     * 使用技术，锂电池
     */
    val technology: String,
    /**
     * 健康状态
     */
    val health: String,
    /**
     * 电压
     */
    val voltage: String
)

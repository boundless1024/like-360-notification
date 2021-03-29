package com.like360.demo.ext

import android.widget.Toast
import com.boundless.ktx.base.BActivity
import com.like360.demo.base.BaseActivity
import com.like360.demo.ui.MainActivity

/**
 * 说明： 扩展类
 * 文件名称：Ext
 * 创建者: hallo
 * 邮箱: hallo@xxx.xx
 * 时间: 2021/3/27 4:42 PM
 * 版本：V1.0.0
 */

public fun BActivity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
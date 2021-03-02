package com.ooftf.spy.lib.test

import android.content.Context
import com.ooftf.master.widget.toolbar.util.ContextUtils

/**
 *
 * @author ooftf
 * @email 994749769@qq.com
 * @date 2021/3/2
 */
object Test {
    fun test(context:Context){
        ContextUtils.toActivity(context)
    }
}
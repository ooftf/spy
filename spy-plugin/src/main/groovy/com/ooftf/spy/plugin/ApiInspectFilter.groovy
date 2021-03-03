package com.ooftf.spy.plugin

import javassist.CtClass
import javassist.CtMethod

/**
 * Created by zhengxiaoyong on 2018/12/24.
 */
interface ApiInspectFilter {

    boolean filter(CtClass clazz)

    boolean filter(String className)

    boolean filter(CtMethod method)

    boolean filter(IncompatibleClassInfo ici)
    boolean filter(IncompatibleMethodInfo ici)

}

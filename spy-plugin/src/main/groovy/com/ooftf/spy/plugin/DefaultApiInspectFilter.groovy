package com.ooftf.spy.plugin

import com.android.SdkConstants
import com.google.common.base.Strings
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

/**
 * Created by zhengxiaoyong on 2018/12/24.
 */
class DefaultApiInspectFilter implements ApiInspectFilter {

    Project mProject

    ApiInspectExtension mApiInspectExtension

    Set<String> mIncludePackages = new HashSet<>()

    Set<String> mExcludePackages = new HashSet<>()

    DefaultApiInspectFilter(Project project) {
        this.mProject = project
        mApiInspectExtension = mProject.extensions.findByType(ApiInspectExtension.class)
    }

    void addIncludePackages(Set<String> packages) {
        mIncludePackages.addAll(packages)
    }

    void addExcludePackages(Set<String> packages) {
        mExcludePackages.addAll(packages)
    }

    @Override
    boolean filter(CtClass clazz) {
        boolean inspectSystemApi = mApiInspectExtension.inspectSystemApi
        return isSystemGenerateClass(clazz.getSimpleName()) || isJavaSystemClass(clazz.getName()) || (inspectSystemApi ? false : isAndroidSystemClass(clazz.getName())) || filterFromExtension(clazz.getName())
    }
    /**
     *
     * @param className
     * @return
     * true: 不 Inspect；
     * false : Inspect
     */
    @Override
    boolean filter(String className) {
        if (Strings.isNullOrEmpty(className)) {
            return true
        }
        boolean inspectSystemApi = mApiInspectExtension.inspectSystemApi
        int index = className.lastIndexOf('.')
        String simpleName = className
        if (index >= 0) {
            simpleName = className.substring(index + 1)
        }
        return isSystemGenerateClass(simpleName) || isJavaSystemClass(className) || (inspectSystemApi ? false : isAndroidSystemClass(className)) || filterFromExtension(className)
    }

    @Override
    boolean filter(CtMethod method) {
        return false
    }

    boolean filterFromExtension(String name) {
        Set<String> includes = mIncludePackages
        Set<String> excludes = mExcludePackages

        boolean filter = false

        if (includes != null && !includes.isEmpty()) {
            for (int i = 0; i < includes.size(); i++) {
                String tmp = includes[i]
                if (Strings.isNullOrEmpty(tmp))
                    continue
                if (name.startsWith(tmp)) {
                    filter = false
                    break
                }
            }
        } else if (excludes != null && !excludes.isEmpty()) {
            for (int i = 0; i < excludes.size(); i++) {
                String tmp = excludes[i]
                if (Strings.isNullOrEmpty(tmp))
                    continue
                if (name.startsWith(tmp)) {
                    filter = true
                    break
                }
            }
        }

        return filter
    }

    boolean isSystemGenerateClass(String name) {
        if (!name.endsWith(SdkConstants.DOT_CLASS))
            name += SdkConstants.DOT_CLASS
        return name.startsWith('R$') || name.contentEquals('R.class') || name.contentEquals("BuildConfig.class")
    }

    boolean isJavaSystemClass(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("org.apache.") || name.startsWith("sun.misc.") || name.startsWith("kotlin.") || name.startsWith("META-INF.")|| name.startsWith("dalvik.")
    }

    boolean isAndroidSystemClass(String name) {
        return name.startsWith("android.") || name.startsWith("androidx.")
    }

}

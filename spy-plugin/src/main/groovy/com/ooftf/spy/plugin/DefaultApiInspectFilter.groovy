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

    Set<FilterItem> mIncludePackages = new HashSet<>()

    Set<FilterItem> mExcludePackages = new HashSet<>()

    DefaultApiInspectFilter(Project project) {
        this.mProject = project
        mApiInspectExtension = mProject.extensions.findByType(ApiInspectExtension.class)
    }

    void addIncludePackages(Set<FilterItem> packages) {
        mIncludePackages.addAll(packages)
    }

    void addExcludePackages(Set<FilterItem> packages) {
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
    boolean filter(IncompatibleClassInfo ici) {
        Set<FilterItem> includes = mIncludePackages
        Set<FilterItem> excludes = mExcludePackages

        boolean filter = false

        if (includes != null && !includes.isEmpty()) {
            for (int i = 0; i < includes.size(); i++) {
                FilterItem tmp = includes[i]
                if (Strings.isNullOrEmpty(tmp.name))
                    continue
                if (ici.incompatibleClassName.startsWith(tmp.name)
                        && (Strings.isNullOrEmpty(tmp.occurName) || ici.className.startsWith(tmp.occurName))) {
                    filter = false
                    break
                }
            }
        } else if (excludes != null && !excludes.isEmpty()) {
            for (int i = 0; i < excludes.size(); i++) {
                FilterItem tmp = excludes[i]
                if (Strings.isNullOrEmpty(tmp.name))
                    continue
                if (ici.incompatibleClassName.startsWith(tmp.name)
                        && (Strings.isNullOrEmpty(tmp.occurName) || ici.className.startsWith(tmp.occurName))) {
                    filter = true
                    break
                }
            }
        }

        return filter
    }

    @Override
    boolean filter(IncompatibleMethodInfo ici) {
        Set<FilterItem> includes = mIncludePackages
        Set<FilterItem> excludes = mExcludePackages

        boolean filter = false

        if (includes != null && !includes.isEmpty()) {
            for (int i = 0; i < includes.size(); i++) {
                FilterItem tmp = includes[i]
                if (Strings.isNullOrEmpty(tmp.name))
                    continue
                if ((Strings.isNullOrEmpty(tmp.name) ||ici.incompatibleClassName.startsWith(tmp.name))
                        && (Strings.isNullOrEmpty(tmp.occurName) || ici.className.startsWith(tmp.occurName))
                        && (Strings.isNullOrEmpty(tmp.method) || ici.methodName.startsWith(tmp.method))
                        && (Strings.isNullOrEmpty(tmp.lines) || ici.lineNumber.startsWith(tmp.lines))) {
                    filter = false
                    break
                }
            }
        } else if (excludes != null && !excludes.isEmpty()) {
            for (int i = 0; i < excludes.size(); i++) {
                FilterItem tmp = excludes[i]
                if (Strings.isNullOrEmpty(tmp.name))
                    continue
                if ((Strings.isNullOrEmpty(tmp.name) ||ici.incompatibleClassName.startsWith(tmp.name))
                        && (Strings.isNullOrEmpty(tmp.occurName) || ici.className.startsWith(tmp.occurName))
                        && (Strings.isNullOrEmpty(tmp.method) || ici.methodName.startsWith(tmp.method))
                        && (Strings.isNullOrEmpty(tmp.lines) || ici.lineNumber.startsWith(tmp.lines)))  {
                    filter = true
                    break
                }
            }
        }

        return filter
    }

    @Override
    boolean filter(CtMethod method) {
        return false
    }


    boolean filterFromExtension(String name) {
        Set<FilterItem> includes = mIncludePackages
        Set<FilterItem> excludes = mExcludePackages

        boolean filter = false

        if (includes != null && !includes.isEmpty()) {
            for (int i = 0; i < includes.size(); i++) {
                FilterItem tmp = includes[i]
                if (Strings.isNullOrEmpty(tmp.name))
                    continue
                if (name.startsWith(tmp.name)) {
                    filter = false
                    break
                }
            }
        } else if (excludes != null && !excludes.isEmpty()) {
            for (int i = 0; i < excludes.size(); i++) {
                FilterItem tmp = excludes[i]
                if (Strings.isNullOrEmpty(tmp.name))
                    continue
                if (name.startsWith(tmp.name)) {
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
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("org.apache.") || name.startsWith("sun.misc.") || name.startsWith("kotlin.") || name.startsWith("META-INF.") || name.startsWith("dalvik.")
    }

    boolean isAndroidSystemClass(String name) {
        return name.startsWith("android.") || name.startsWith("androidx.")
    }

}

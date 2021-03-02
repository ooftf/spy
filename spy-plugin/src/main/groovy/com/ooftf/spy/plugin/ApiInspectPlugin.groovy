package com.ooftf.spy.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by ooftf on 2021/3/2.
 */
class ApiInspectPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("spy", ApiInspectExtension.class, project)
        if (project.plugins.hasPlugin("com.android.library")) {
            return
        }
        def android = null
        if (project.plugins.hasPlugin("com.android.application")) {
            android = project.extensions.getByType(AppExtension.class)
        }
        if (android == null)
            return
        android.registerTransform(new ApiInspectTransform(project))
    }
}

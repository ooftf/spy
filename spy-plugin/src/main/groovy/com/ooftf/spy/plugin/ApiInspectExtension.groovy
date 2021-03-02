package com.ooftf.spy.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory

/**
 * Created by ooftf on 2021/3/2.
 */
class ApiInspectExtension {

    boolean enable = true

    boolean inspectSystemApi = false
    boolean interruptBuild = true
    ApiInspectIncludeExtension include

    ApiInspectExcludeExtension exclude

    ApiInspectExtension(Project project) {
        try {
            ObjectFactory objectFactory = project.getObjects()
            include = objectFactory.newInstance(ApiInspectIncludeExtension.class)
            exclude = objectFactory.newInstance(ApiInspectExcludeExtension.class)
        } catch (Exception e) {
            include = ApiInspectIncludeExtension.class.newInstance()
            exclude = ApiInspectExcludeExtension.class.newInstance()
        }
    }

    void include(Action<ApiInspectIncludeExtension> action) {
        action.execute(include)
    }

    void exclude(Action<ApiInspectExcludeExtension> action) {
        action.execute(exclude)
    }

}

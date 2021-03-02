package com.ooftf.spy.plugin

import com.google.common.base.Strings

/**
 * Created by ooftf on 2021/3/2.
 */
class ApiInspectExcludeExtension {

    Set<String> apis = new HashSet<>()

    void api(String api) {
        if (Strings.isNullOrEmpty(api))
            return
        apis.add(api)
    }

}

package com.ooftf.spy.plugin

import com.google.common.base.Strings

/**
 * Created by ooftf on 2021/3/2.
 */
class ApiInspectIncludeExtension {

    Set<FilterItem> apis = new HashSet<>()


    void api(String name ="", String occurName ="", String method ="", String lineNumber ="") {
        if (Strings.isNullOrEmpty(name)&&Strings.isNullOrEmpty(occurName)&&Strings.isNullOrEmpty(method)&&Strings.isNullOrEmpty(lineNumber))
            return
        FilterItem item = new FilterItem()
        item.name = name
        item.occurName = occurName
        item.method = method
        item.lines = lineNumber
        apis.add(item)
    }


}

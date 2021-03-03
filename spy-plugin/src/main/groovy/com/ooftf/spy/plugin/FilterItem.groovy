package com.ooftf.spy.plugin
/**
 * Created by ooftf on 2021/3/2.
 */
class FilterItem {
    String name = ""
    String occurName = ""
    String method = ""
    String lines = ""

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof FilterItem)) return false

        FilterItem that = (FilterItem) o

        if (lines != that.lines) return false
        if (method != that.method) return false
        if (name != that.name) return false
        if (occurName != that.occurName) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (occurName != null ? occurName.hashCode() : 0)
        result = 31 * result + (method != null ? method.hashCode() : 0)
        result = 31 * result + (lines != null ? lines.hashCode() : 0)
        return result
    }
}
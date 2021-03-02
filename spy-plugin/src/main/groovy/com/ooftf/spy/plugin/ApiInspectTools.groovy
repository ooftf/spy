package com.ooftf.spy.plugin

import com.android.SdkConstants
import com.android.utils.FileUtils
import com.google.common.base.Strings
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.Project

/**
 * Created by zhengxiaoyong on 2018/12/23.
 */
class ApiInspectTools {

    static void exportApiInspectResult(Project project, String variant, Set<IncompatibleClassInfo> incompatibleClassInfoSet, Set<IncompatibleMethodInfo> incompatibleMethodInfoSet) {
        File target = new File(project.buildDir, "api-inspect" + File.separator + variant + File.separator + "inspect-result.txt")
        target.parentFile.mkdirs()
        FileUtils.deleteIfExists(target)
        target.createNewFile()
        BufferedWriter writer = target.newWriter("UTF-8", true)

        int count = incompatibleClassInfoSet.size() + incompatibleMethodInfoSet.size()
        writer.write("=====================================>Inspect Results<=====================================\n")
        writer.write(">>> Count: [$count]\n")
        writer.write("--------------------------------------------------------------------------------------------\n")
        if (!incompatibleClassInfoSet.isEmpty()) {
            incompatibleClassInfoSet.each {
                writer.write("Incompatible Api -> [Class: ${it.incompatibleClassName}]\n")
                writer.write("                 └> [Occur In Class : ${it.className}]\n")
                writer.write("--------------------------------------------------------------------------------------------\n")
            }
        }

        if (!incompatibleMethodInfoSet.isEmpty()) {
            incompatibleMethodInfoSet.each {
                writer.write("Incompatible Api -> [Class: ${it.incompatibleClassName}]\n")
                writer.write("                 └> [Method: ${it.methodName}]\n")
                writer.write("                 └> [Occur In Class: ${it.className}, Line: ${it.lineNumber}]\n")
                writer.write("--------------------------------------------------------------------------------------------\n")
            }
        }

        if (incompatibleClassInfoSet.isEmpty() && incompatibleMethodInfoSet.isEmpty()) {
            writer.write("> NONE.\n")
        }
        writer.write("============================================================================================\n\n")
        writer.flush()
        writer.close()
    }


}

package com.ooftf.spy.plugin

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.google.common.collect.ImmutableSet
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.GradleException
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Created by ooftf on 2021/3/2.
 */
class ApiInspectTransform extends Transform {

    Project mProject

    ClassPool mClassPool

    ApiInspector mApiInspector

    DefaultApiInspectFilter mApiInspectFilter

    ApiInspectExtension mApiInspectExtension


    Set<File> mJarFilePaths = new HashSet<>()
    Set<File> mProjectFilePaths = new HashSet<>()

    ApiInspectTransform(Project project) {
        this.mProject = project
        mApiInspectExtension = mProject.extensions.findByType(ApiInspectExtension.class)
        mApiInspectFilter = new DefaultApiInspectFilter(project)
    }

    @Override
    String getName() {
        return "apiInspect"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return ImmutableSet.of()
    }

    @Override
    Set<? super QualifiedContent.Scope> getReferencedScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        if (!mApiInspectExtension.enable) {
            return
        }
        initialize()
        if (transformInvocation.isIncremental() && isIncremental()) {
            transformInvocation.referencedInputs.each { TransformInput input ->
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    Map<File, Status> changedFiles = directoryInput.getChangedFiles()
                    for (Map.Entry<File, Status> changedInput : changedFiles.entrySet()) {
                        File changeInputFile = changedInput.getKey()
                        Status status = changedInput.getValue()
                        switch (status) {
                            case Status.NOTCHANGED:
                                mClassPool.appendClassPath(directoryInput.file.absolutePath)
                                break
                            case Status.ADDED:
                            case Status.CHANGED:
                                mClassPool.appendClassPath(directoryInput.file.absolutePath)
                                mProjectFilePaths.add(directoryInput.file)
                                break
                            case Status.REMOVED:
                                break
                            default:
                                break
                        }
                    }
                }

                input.jarInputs.each { JarInput jarInput ->
                    File jarFile = jarInput.file
                    switch (jarInput.status) {
                        case Status.NOTCHANGED:
                            mClassPool.appendClassPath(jarFile.absolutePath)
                            break
                        case Status.ADDED:
                        case Status.CHANGED:
                            mClassPool.appendClassPath(jarFile.absolutePath)
                            mJarFilePaths.add(jarFile)
                            break
                        case Status.REMOVED:
                            FileUtils.deleteIfExists(jarFile)
                            break
                    }
                }
            }
        } else {
            transformInvocation.referencedInputs.each { TransformInput input ->
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    mClassPool.appendClassPath(directoryInput.file.absolutePath)
                    mProjectFilePaths.add(directoryInput.file)
                }

                input.jarInputs.each { JarInput jarInput ->
                    mClassPool.appendClassPath(jarInput.file.absolutePath)
                    mJarFilePaths.add(jarInput.file)
                }
            }
        }
        mProjectFilePaths.each { projectFile ->
            String root = projectFile.absolutePath
            projectFile.eachFileRecurse { File file ->
                if (file.isFile()) {
                    def entryName = file.absolutePath.replace(root, '')
                    inspectFile(entryName)
                }
            }
        }
        mJarFilePaths.each { jarFile ->
            JarFile jar = new JarFile(jarFile)
            Enumeration<JarEntry> jarEntries = jar.entries()
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement()
                if (jarEntry.isDirectory())
                    continue
                String entryName = jarEntry.getName()
                inspectFile(entryName)
            }
        }
        Set<IncompatibleClassInfo> incompatibleClassInfoSet = mApiInspector.getIncompatibleClasses()
        Set<IncompatibleMethodInfo> incompatibleMethodInfoSet = mApiInspector.getIncompatibleMethods()
        System.err.print("InspectApi end ,has error = " + (!incompatibleClassInfoSet.isEmpty() || !incompatibleMethodInfoSet.isEmpty()))
        if (!incompatibleClassInfoSet.isEmpty() || !incompatibleMethodInfoSet.isEmpty()) {
            int count = incompatibleClassInfoSet.size() + incompatibleMethodInfoSet.size()
            mProject.logger.error("\n=====================================>Api Incompatible<=====================================")
            mProject.logger.error(">>> Count: [$count]")
            mProject.logger.error("--------------------------------------------------------------------------------------------")
            incompatibleClassInfoSet.each {
                mProject.logger.error("Incompatible Api -> [Class: ${it.incompatibleClassName}]")
                mProject.logger.error("                 └> [Occur in class : ${it.className}]")
                mProject.logger.error("--------------------------------------------------------------------------------------------")
            }
            incompatibleMethodInfoSet.each {
                mProject.logger.error("Incompatible Api -> [Class: ${it.incompatibleClassName}]")
                mProject.logger.error("                 └> [Method: ${it.methodName}]")
                mProject.logger.error("                 └> [Occur in class: ${it.className}, Line: ${it.lineNumber}]")
                mProject.logger.error("--------------------------------------------------------------------------------------------")
            }
            mProject.logger.error("============================================================================================\n")
        }

        def variant = transformInvocation.context.variantName
        ApiInspectTools.exportApiInspectResult(mProject, variant, incompatibleClassInfoSet, incompatibleMethodInfoSet)
        mClassPool.clearImportedPackages()
        if (mApiInspectExtension.interruptBuild && (!incompatibleClassInfoSet.isEmpty() || !incompatibleMethodInfoSet.isEmpty())) {
            throw SpyException("class error:" + incompatibleClassInfoSet.size() + "; method error:" + incompatibleMethodInfoSet.size())
        }

    }


    def setupConfig() {
        ApiInspectIncludeExtension includeExtension = mApiInspectExtension.include
        ApiInspectExcludeExtension excludeExtension = mApiInspectExtension.exclude

        if (includeExtension != null && includeExtension.apis != null)
            mApiInspectFilter.addIncludePackages(includeExtension.apis)

        if (excludeExtension != null && excludeExtension.apis != null)
            mApiInspectFilter.addExcludePackages(excludeExtension.apis)
    }

    def inspectFile(String entryName) {
        if (entryName.endsWith(SdkConstants.DOT_CLASS)) {
            String className = entryName.replace('/', '.').replace('\\', '.').replace(File.separator, '.')
            def start = 0
            if (className.startsWith(".")) {
                start = 1
            }
            className = className.substring(start, className.length() - SdkConstants.DOT_CLASS.length())
            try {
                CtClass clazz = mClassPool.getCtClass(className)
                mApiInspector.inspectClass(mClassPool, clazz)
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    def initialize() {
        mClassPool = new ClassPool()
        mApiInspector = new ApiInspector(mProject, mApiInspectFilter)

        setupConfig()

        def android
        if (mProject.plugins.hasPlugin("com.android.application")) {
            android = mProject.extensions.getByType(AppExtension.class)
        } else {
            throw new GradleException("The plugin type is not supported！")
        }

        def androidJar = "${android.getSdkDirectory().getAbsolutePath()}${File.separator}platforms${File.separator}" +
                "${android.getCompileSdkVersion()}${File.separator}android.jar"
        mClassPool.appendClassPath(androidJar)
    }

}

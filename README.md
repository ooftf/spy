# **Spy**

在Build项目时检查Class之间的引用关系，避免因为依赖库版本问题而产生的ClassNotFoundException问题  
本项目借鉴于https://github.com/Sunzxyong/ApiInspect 修改内容如下
* 增加了检测范围；
* 支持className occurClassName method LineNumber多条件过滤
* 修改为Referenced方式，不再Copy文件
* 优化了兼容性问题；
* 添加了interruptBuild属性，可选择打断Build过程

-------------------------------------------------------------------------------

[ ![Download](https://api.bintray.com/packages/ooftf/maven/spy-plugin/images/download.svg) ](https://bintray.com/ooftf/maven/spy-plugin/_latestVersion)
## **Introduce**


## **Usage**
### **Installation**
Add dependencies in **`build.gradle`** of the **`root project`**：

```
    repositories {
        maven {
            url "https://dl.bintray.com/ooftf/maven"
        }
   
    }
    dependencies {
        // ...
        classpath('com.ooftf:spy-plugin:1.0.1') {
            exclude group: 'com.android.tools.build', module: 'gradle'
        }
    }
```

and add the **`apply plugin`** to build.gradle in the module：

```
apply plugin: 'ooftf-spy'
```

### **Configuration**
By default, **`spy`** will inspects all apis but does not contain the system api. Of course, you can also customize **`exclude`** or **`include`** Settings：

```
spy {

    enable true //Whether api inspect is enabled.

    inspectSystemApi false //Whether to inspect the system api.
    interruptBuild true //Whether interrupt build when find error
    //Specify the library to inspect.
//    include {
//        //Value is the package name.
//        api "com.zxy.tiny"
//    }

    //Specify the library not to inspect.
       /*exclude {
        //Value is the package name.
        api 'com.taobao.'
        api 'com.alibaba.'
        api 'org.'
        api 'com.baidubce.'
        api 'com.heytap.'
        api 'com.meizu.'
        api 'com.huawei.'
        api 'com.google.'
        api 'com.vivo.'
        api 'edu.umd.cs.findbugs.annotations.SuppressFBWarnings'
        api 'com.squareup.picasso.'
        api 'com.alipay.'
        api 'dalvik.'
        api 'sun.nio.'
        api 'okhttp3.internal.annotations.EverythingIsNonNull'
        api 'com.nostra13.universalimageloader.','com.didichuxing.doraemonkit'
        api 'com.tencent.map.geolocation.','com.didichuxing.doraemonkit'
    }*/

}
```
-------

    刚刚添spy可能报错比较多不要慌，一步步配置exclude即可，  
    首先根据报错信息找到对应的类，如果对应位置有try catch处理这些一般可以添加到exclude
    还有一些是不会运行到的类也可以添加到exclude
## **Inspect Result**
When the Apk build is completed. The results of the inspection will be printed on the console：

<img src="https://raw.githubusercontent.com/Sunzxyong/ImageRepository/master/apiinspect.png" width="500"/>

Of course, The results of the inspection will also be stored in the **`api-inspect`** directory：

<img src="https://raw.githubusercontent.com/Sunzxyong/ImageRepository/master/apiinspect_result.jpg" width="500"/>



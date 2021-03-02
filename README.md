# **Spy**
在Build项目时检查Class之间的引用关系，避免因为依赖库版本问题而产生的ClassNotFoundException问题

----

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
        classpath('com.ooftf:spy-plugin:1.0.0') {
            exclude group: 'com.android.tools.build', module: 'gradle'
        }
    }
```

and add the **`apply plugin`** to build.gradle in the module：

```
apply plugin: 'ooftf-spy'
```

### **Configuration**
By default, **`ApiInspect`** will inspects all apis but does not contain the system api. Of course, you can also customize **`exclude`** or **`include`** Settings：

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
//    exclude {
//        //Value is the package name.
//        api 'com.zxy.tiny'
//        api 'com.google.zxing'
//    }

}
```

## **Inspect Result**
When the Apk build is completed. The results of the inspection will be printed on the console：

<img src="https://raw.githubusercontent.com/Sunzxyong/ImageRepository/master/apiinspect.png" width="500"/>

Of course, The results of the inspection will also be stored in the **`api-inspect`** directory：

<img src="https://raw.githubusercontent.com/Sunzxyong/ImageRepository/master/apiinspect_result.jpg" width="500"/>






## **License**

>
>     Apache License
>
>     Version 2.0, January 2004
>     http://www.apache.org/licenses/
>
>     Copyright 2018 郑晓勇
>
>  Licensed under the Apache License, Version 2.0 (the "License");
>  you may not use this file except in compliance with the License.
>  You may obtain a copy of the License at
>
>      http://www.apache.org/licenses/LICENSE-2.0
>
>  Unless required by applicable law or agreed to in writing, software
>  distributed under the License is distributed on an "AS IS" BASIS,
>  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
>  See the License for the specific language governing permissions and
>  limitations under the License.



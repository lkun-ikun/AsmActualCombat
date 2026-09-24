# AsmActualCombat
**使用文档链接:https://github.com/Peakmain/AsmActualCombat/wiki**

### How To

#### 旧版本添加方式

**ASM插件依赖**
Add it in your root build.gradle at the end of repositories:
```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.github.peakmain:plugin:1.1.2"
  }
}

apply plugin: "com.peakmain.plugin"
```
**拦截事件sdk的依赖**
- Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
- Step 2. Add the dependency
```
	dependencies {
	       implementation 'com.github.Peakmain:AsmActualCombat:1.1.4'
	}
```
#### 新版本添加方式

**settings.gradle**
```
pluginManagement {
    repositories {
        //插件依赖
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        //sdk仓库
        maven { url 'https://jitpack.io' }
    }
}
```

**插件依赖**

根目录下的build.gradle文件
```
plugins {
    //插件依赖和版本
    id "io.github.peakmain" version "1.1.2" apply false
}
```
**sdk版本依赖**

```
implementation 'com.github.Peakmain:AsmActualCombat:1.1.4'
```

### 功能介绍
#### ASM全埋点功能
- AppStart事件：应用程序启动启动事件。
- AppEnd事件:应用程序退出事件。
- AppViewScreen事件:应用程序页面浏览事件
- AppClick 事件:应用程序控件（View）点击事件,如:ImageView,Button,Dialog等
- 默认包含防止多次点击事件的处理
- 可动态设置方法对点击事件处理之前进行拦截，目前只支持对setOnClickListener进行拦截
##### 隐私方法调用处理
- 对调用隐私方法的方法体替换成自己的方法(支持动态替换方法)
- 支持的方法如下
![image](https://user-images.githubusercontent.com/26482737/170660484-740f1399-3a28-4245-9e2b-adc4fb268633.png)
```
monitorPlugin {
    whiteList = [//设置白名单
            "com.peakmain.asmactualcombat.utils.TestUtils",
            "com.peakmain.plugin"
    ]
    methodStatus = 1//1代表方法体替换，其他都是正常情况
    enableLog=true//是否开启日志打印，默认不打印
}

```

#### 辅助功能
- 可获取方法的耗时时间
- 打印方法的参数和返回值
- 打印方法的Frame
- 可动态配置是否开启插件，默认是开启



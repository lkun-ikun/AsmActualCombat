package com.peakmain.analytics.plugin.transform

import org.objectweb.asm.ClassWriter

/**
 * 用类层级索引重写 getCommonSuperClass 的 ClassWriter。
 *
 * 背景：原来的写法是 ClassWriter.COMPUTE_MAXS + ClassReader.SKIP_FRAMES，
 * 改写后的 class 完全没有 StackMapTable，D8 会对每个含分支的方法报警告
 * （Expected stack map table for method with non-linear control flow）。
 * 直接改成 COMPUTE_FRAMES 又会因为默认的 getCommonSuperClass 使用插件自身的
 * ClassLoader 去加载工程/第三方类而失败（异常被吞掉后整个类不会被改写）。
 * 所以这里改成：帧全部重算，类型关系从 transform 的输入 classpath 解析。
 */
class SafeClassWriter extends ClassWriter {

    private final ClasspathClassIndex classIndex

    SafeClassWriter(int flags, ClasspathClassIndex classIndex) {
        super(flags)
        this.classIndex = classIndex
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        if (classIndex == null) {
            return 'java/lang/Object'
        }
        try {
            String common = classIndex.commonSuperClass(type1, type2)
            return common == null ? 'java/lang/Object' : common
        } catch (Throwable ignored) {
            // 任何解析异常都退化成 Object：语义上仍然是合法（更保守）的公共父类型
            return 'java/lang/Object'
        }
    }
}

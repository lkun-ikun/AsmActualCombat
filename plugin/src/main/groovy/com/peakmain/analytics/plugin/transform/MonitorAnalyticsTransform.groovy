package com.peakmain.analytics.plugin.transform

import com.peakmain.analytics.plugin.ext.MonitorConfig
import com.peakmain.analytics.plugin.visitor.PeakmainVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.utils.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.regex.Matcher

class MonitorAnalyticsTransform {
    private static HashSet<String> exclude = new HashSet<>(['com.peakmain.sdk',
                                                            'android.support',
                                                            'androidx',
                                                            'com.google.android',
                                                            'com.bumptech.glide',
                                                            'com.tencent.tinker'])
    /** 将一些特例需要排除在外 */
    private static final HashSet<String> special = ['android.support.design.widget.TabLayout$ViewPagerOnTabSelectedListener',
                                                    'com.google.android.material.tabs.TabLayout$ViewPagerOnTabSelectedListener',
                                                    'android.support.v7.app.ActionBarDrawerToggle',
                                                    'androidx.appcompat.app.ActionBarDrawerToggle',
                                                    'androidx.fragment.app.FragmentActivity',
                                                    'androidx.core.app.NotificationManagerCompat',
                                                    'androidx.core.app.ComponentActivity',
                                                    'android.support.v4.app.NotificationManagerCompat',
                                                    'android.support.v4.app.SupportActivity',
                                                    'cn.jpush.android.service.PluginMeizuPlatformsReceiver',
                                                    'androidx.appcompat.widget.ActionMenuPresenter$OverflowMenuButton',
                                                    'android.widget.ActionMenuPresenter$OverflowMenuButton',
                                                    'android.support.v7.widget.ActionMenuPresenter$OverflowMenuButton']
    /**
     * 过滤不需要修改的class
     */
    protected static boolean isShouldModify(String className) {
        boolean isShouldModify = false
        if (!isAndroidGenerated(className)) {
            for (pkgName in special) {
                if (className.startsWith(pkgName)) {
                    return true
                }
            }
            isShouldModify = true
            if (!isLeanback(className)) {
                for (pkgName in exclude) {
                    if (className.startsWith(pkgName)) {
                        isShouldModify = false
                        break
                    }
                }
            }
        }
        return isShouldModify
    }

    private static boolean isLeanback(String className) {
        return className.startsWith("android.support.v17.leanback") || className.startsWith("androidx.leanback")
    }

    private static boolean isAndroidGenerated(String className) {
        return className.contains('R$') ||
                className.contains('R2$') ||
                className.contains('R.class') ||
                className.contains('R2.class') ||
                className.contains('BuildConfig.class')
    }

    static File modifyClassFile(File dir, File classFile, File tempDir, MonitorConfig monitorConfig, ClasspathClassIndex classIndex) {
        File modified = null
        try {
            String className = path2ClassName(classFile.absolutePath.replace(dir.absolutePath + File.separator, ""))
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(classFile))
            byte[] modifiedClassBytes = modifyClass(sourceClassBytes, monitorConfig, classIndex)
            if (modifiedClassBytes) {
                modified = new File(tempDir, className.replace('.', '') + '.class')
                if (modified.exists()) {
                    modified.delete()
                }
                modified.createNewFile()
                new FileOutputStream(modified).write(modifiedClassBytes)
            }
        } catch (Exception e) {
            e.printStackTrace()
            modified = classFile
        }
        return modified
    }

    private static byte[] modifyClass(byte[] srcClass, MonitorConfig monitorConfig, ClasspathClassIndex classIndex) throws IOException {
        // 说明：原来是 COMPUTE_MAXS + SKIP_FRAMES，改写后没有任何 StackMapTable，
        // D8 会对每个含分支的方法发 "Expected stack map table ..." 警告。
        // 现在：
        //   1) 没有实际改动 → 直接返回 null（调用方保留原始字节，原始栈帧不受影响，也省掉算帧开销）
        //   2) 有实际改动 → COMPUTE_FRAMES 全量重算 + SafeClassWriter（类型关系取自 transform 的 classpath）
        ClassWriter classWriter = new SafeClassWriter(ClassWriter.COMPUTE_FRAMES, classIndex)
        PeakmainVisitor classVisitor = new PeakmainVisitor(classWriter, monitorConfig)
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES)
        if (!classVisitor.changed) {
            // 没有实际改动时：
            //   - 原始字节已带 StackMapTable（或 class 版本 < 50，本来就不需要帧）→ 原样返回，省掉算帧开销
            //   - 原始字节缺帧且版本 >= 50（老编译产物）→ D8 仍会报警告，继续走 COMPUTE_FRAMES 补齐
            int majorVersion = ((srcClass[6] & 0xff) << 8) | (srcClass[7] & 0xff)
            if (majorVersion < 50 || hasStackMapTable(srcClass)) {
                return null
            }
        }
        return classWriter.toByteArray()
    }

    /**
     * class 文件里是否已经存在 StackMapTable 属性。
     * 用 ISO-8859-1 转字符串后做一次查找（等价于字节搜索，但不走 Groovy 逐字节循环，速度差异很大）。
     */
    private static boolean hasStackMapTable(byte[] classBytes) {
        if (classBytes == null || classBytes.length < 15) {
            return false
        }
        String text = new String(classBytes, java.nio.charset.StandardCharsets.ISO_8859_1)
        return text.contains('StackMapTable')
    }

    static File modifyJar(File jarFile, File tempDir, boolean nameHex, MonitorConfig monitorConfig, ClasspathClassIndex classIndex) {
        /**
         * 读取原 jar
         */
        def file = new JarFile(jarFile, false)

        /**
         * 设置输出到的 jar
         */
        def hexName = ""
        if (nameHex) {
            hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        }
        def outputJar = new File(tempDir, hexName + jarFile.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar))
        Enumeration enumeration = file.entries()
        boolean anyChanged = false
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream
            try {
                inputStream = file.getInputStream(jarEntry)
            } catch (Exception e) {
                return null
            }
            String entryName = jarEntry.getName()
            if (entryName.endsWith(".DSA") || entryName.endsWith(".SF")) {
                //ignore
            } else {
                String className
                JarEntry jarEntry2 = new JarEntry(entryName)
                jarOutputStream.putNextEntry(jarEntry2)

                byte[] modifiedClassBytes = null
                byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
                if (entryName.endsWith(".class")) {
                    className = entryName.replace(Matcher.quoteReplacement(File.separator), ".").replace(".class", "")
                    if (isShouldModify(className)) {
                        modifiedClassBytes = modifyClass(sourceClassBytes, monitorConfig, classIndex)
                        if (modifiedClassBytes != null) {
                            anyChanged = true
                        }
                    }
                }
                if (modifiedClassBytes == null) {
                    modifiedClassBytes = sourceClassBytes
                }
                jarOutputStream.write(modifiedClassBytes)
                jarOutputStream.closeEntry()
            }
        }
        jarOutputStream.close()
        file.close()
        // 一个都没改动的话不值得重写整个 jar（省掉解压/压缩全部条目的开销），直接让调用方用原 jar
        if (!anyChanged) {
            outputJar.delete()
            return null
        }
        return outputJar
    }

    static String path2ClassName(String pathName) {
        pathName.replace(File.separator, ".").replace(".class", "")
    }
}

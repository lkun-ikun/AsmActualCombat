package com.peakmain.analytics.plugin.transform

import org.objectweb.asm.ClassReader

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * 类层级索引：为 ASM 的 COMPUTE_FRAMES 提供 getCommonSuperClass 需要的类型信息。
 *
 * 数据来源 = transform 的全部输入（依赖 jar + 工程 class 目录）+ android.jar（boot classpath）。
 * 只读取 class 头部（skip code/debug/frames），并按需缓存，避免整体加载到内存。
 */
class ClasspathClassIndex {

    static class ClassInfo {
        String name
        String superName
        String[] interfaces = new String[0]
        boolean isInterface
    }

    private final Map<String, File> jarIndex = new ConcurrentHashMap<String, File>()
    private final Map<File, ZipFile> openZips = new ConcurrentHashMap<File, ZipFile>()
    private final List<File> dirRoots = new ArrayList<File>()
    private final Map<String, ClassInfo> cache = new ConcurrentHashMap<String, ClassInfo>()
    private final AtomicInteger missCount = new AtomicInteger()

    ClasspathClassIndex(Collection<File> roots) {
        roots?.each { File root ->
            if (root == null || !root.exists()) {
                return
            }
            if (root.isDirectory()) {
                dirRoots.add(root)
                return
            }
            String lower = root.name.toLowerCase(Locale.ROOT)
            if (!lower.endsWith('.jar') && !lower.endsWith('.zip')) {
                return
            }
            try {
                ZipFile zipFile = new ZipFile(root)
                openZips.put(root, zipFile)
                Enumeration<? extends ZipEntry> entries = zipFile.entries()
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement()
                    String name = entry.getName()
                    if (!entry.isDirectory() && name.endsWith('.class')) {
                        jarIndex.put(name.substring(0, name.length() - 6), root)
                    }
                }
            } catch (Exception ignored) {
                // 读不了的 jar 直接跳过，解析不到类型时会安全退化
            }
        }
    }

    int getMissCount() {
        return missCount.get()
    }

    void close() {
        openZips.values().each { ZipFile zipFile ->
            try {
                zipFile.close()
            } catch (Exception ignored) {
            }
        }
        openZips.clear()
        jarIndex.clear()
        cache.clear()
    }

    ClassInfo getInfo(String internalName) {
        if (internalName == null) {
            return null
        }
        ClassInfo cached = cache.get(internalName)
        if (cached != null) {
            return cached
        }
        byte[] bytes = readClassBytes(internalName)
        if (bytes == null) {
            missCount.incrementAndGet()
            return null
        }
        try {
            ClassReader reader = new ClassReader(bytes)
            ClassInfo info = new ClassInfo()
            info.name = reader.getClassName()
            info.superName = reader.getSuperName()
            info.interfaces = reader.getInterfaces() ?: new String[0]
            info.isInterface = (reader.getAccess() & 0x0200) != 0 // ACC_INTERFACE
            cache.put(internalName, info)
            return info
        } catch (Exception ignored) {
            missCount.incrementAndGet()
            return null
        }
    }

    private byte[] readClassBytes(String internalName) {
        String resource = internalName + '.class'

        for (File root : dirRoots) {
            File file = new File(root, resource)
            if (file.isFile()) {
                try {
                    return file.bytes
                } catch (Exception ignored) {
                }
            }
        }

        File jar = jarIndex.get(internalName)
        if (jar == null) {
            return null
        }
        ZipFile zipFile = openZips.get(jar)
        if (zipFile == null) {
            return null
        }
        try {
            ZipEntry entry = zipFile.getEntry(resource)
            if (entry == null) {
                return null
            }
            InputStream inputStream = zipFile.getInputStream(entry)
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Math.max(64, (int) entry.getSize()))
                byte[] buffer = new byte[8192]
                int read
                while ((read = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, read)
                }
                return outputStream.toByteArray()
            } finally {
                inputStream.close()
            }
        } catch (Exception ignored) {
            return null
        }
    }

    /**
     * 等价于 ASM ClassWriter#getCommonSuperClass，但类型信息来自本索引而不是 ClassLoader。
     */
    String commonSuperClass(String type1, String type2) {
        if (type1 == null || type2 == null) {
            return 'java/lang/Object'
        }
        if (type1 == type2) {
            return type1
        }
        if (type1.startsWith('[') || type2.startsWith('[')) {
            return mergeArrayTypes(type1, type2)
        }
        ClassInfo info1 = getInfo(type1)
        ClassInfo info2 = getInfo(type2)
        if (info1 == null || info2 == null) {
            return 'java/lang/Object'
        }
        if (isAssignable(type1, type2)) {
            return type1
        }
        if (isAssignable(type2, type1)) {
            return type2
        }
        if (info1.isInterface || info2.isInterface) {
            return 'java/lang/Object'
        }
        ClassInfo current = getInfo(info1.superName)
        while (current != null) {
            if (isAssignable(current.name, type2)) {
                return current.name
            }
            current = getInfo(current.superName)
        }
        return 'java/lang/Object'
    }

    private String mergeArrayTypes(String type1, String type2) {
        int dim1 = arrayDimensions(type1)
        int dim2 = arrayDimensions(type2)
        if (dim1 < 0 || dim2 < 0 || dim1 != dim2) {
            return 'java/lang/Object'
        }
        String element1 = type1.substring(dim1)
        String element2 = type2.substring(dim2)
        if (element1 == element2) {
            return type1
        }
        if (element1.startsWith('L') && element2.startsWith('L')) {
            String name1 = element1.substring(1, element1.length() - 1)
            String name2 = element2.substring(1, element2.length() - 1)
            String common = commonSuperClass(name1, name2)
            return ('[' * dim1) + 'L' + common + ';'
        }
        return 'java/lang/Object'
    }

    private static int arrayDimensions(String type) {
        if (type == null) {
            return -1
        }
        int count = 0
        char openBracket = '['.charAt(0)
        while (count < type.length() && type.charAt(count) == openBracket) {
            count++
        }
        if (count == 0 || count >= type.length()) {
            return count == 0 ? -1 : count
        }
        char element = type.charAt(count)
        if (element == 'L') {
            return type.endsWith(';') ? count : -1
        }
        return count
    }

    private boolean isAssignable(String superType, String subType) {
        if (superType == null || subType == null) {
            return false
        }
        if (superType == subType) {
            return true
        }
        if (superType == 'java/lang/Object') {
            return true
        }
        Set<String> visited = new HashSet<String>()
        Deque<String> queue = new ArrayDeque<String>()
        queue.add(subType)
        while (!queue.isEmpty()) {
            String current = queue.poll()
            if (current == null || !visited.add(current)) {
                continue
            }
            if (current == superType) {
                return true
            }
            ClassInfo info = getInfo(current)
            if (info == null) {
                continue
            }
            if (info.superName != null) {
                queue.add(info.superName)
            }
            for (String interfaceName : info.interfaces) {
                if (interfaceName != null) {
                    queue.add(interfaceName)
                }
            }
        }
        return false
    }
}

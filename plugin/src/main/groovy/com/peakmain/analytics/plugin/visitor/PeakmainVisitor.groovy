package com.peakmain.analytics.plugin.visitor

import com.peakmain.analytics.plugin.entity.PeakmainMethodCell
import com.peakmain.analytics.plugin.ext.MonitorConfig
import com.peakmain.analytics.plugin.utils.MethodFieldUtils
import com.peakmain.analytics.plugin.utils.OpcodesUtils
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
/**
 * author ：Peakmain
 * createTime：2021/1/5
 * mail:2726449200@qq.com
 * describe：
 */
class PeakmainVisitor extends ClassVisitor {
    /**
     * 本次改写是否真的修改过字节码。
     * 只有 true 时才需要用 COMPUTE_FRAMES 重算栈帧；没改动的类直接返回原始字节，
     * 既省掉大量计算，也天然保留原始 StackMapTable。
     * 注意：如果以后启用下面被注释掉的 adapter，请在对应 adapter 里同样上报改动。
     */
    public boolean changed = false
    private ClassVisitor classVisitor
    private String[] mInterfaces
    private HashMap<String, PeakmainMethodCell> mMethodCells = new HashMap<>()
    private String mClassName
    private MonitorConfig mMonitorConfig

    PeakmainVisitor(ClassVisitor classVisitor, MonitorConfig config) {
        super(OpcodesUtils.ASM_VERSION, classVisitor)
        this.classVisitor = classVisitor
        this.mMonitorConfig = config

    }
    /**
     * @param version 类版本
     * @param access 修饰符
     * @param name 类名
     * @param signature 泛型信息
     * @param superName 父类
     * @param interfaces 实现的接口
     */
    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.mInterfaces = interfaces
        this.mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }
    /**
     * 扫描类的方法进行调用
     * @param access 修饰符
     * @param name 方法名字
     * @param descriptor 方法签名
     * @param signature 泛型信息
     * @param exceptions 抛出的异常
     * @return
     */
    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
//        methodVisitor = new MonitorClickAdapter(methodVisitor, access, name, descriptor, mMethodCells, mInterfaces, mMonitorConfig, mClassName)
//        methodVisitor = new MonitorPrintParametersReturnValueAdapter(methodVisitor, access, name, descriptor, mClassName, classVisitor)
        if (mMonitorConfig.getStatusEnum() == MethodFieldUtils.StatusEnum.METHOD_STATE_REPLACE) {
            methodVisitor = new MonitorMethodCalledReplaceAdapter(methodVisitor, access, name, descriptor, classVisitor, mClassName, mMonitorConfig, this)
        }
//        if (mMonitorConfig.isInterceptNetworks) {
//            methodVisitor = new NetworkMethodCalledReplaceAdapter(methodVisitor, access, name, descriptor, classVisitor,mClassName,mMonitorConfig)
//        }
//        if (!mMonitorConfig.disableStackMapFrame)
//            methodVisitor = new MonitorMethodStackMapFrameAdapter(mClassName, access, name, descriptor, methodVisitor)

//        methodVisitor = new OvenAdapter(methodVisitor, access, name, descriptor, mClassName)
//        methodVisitor = new MonitorMethodCalledClearAdapter(methodVisitor, access, name, descriptor, mClassName, mMonitorConfig)
        return methodVisitor
    }

    /**
     * 获取方法参数下标为 index 的对应 ASM index
     * @param types 方法参数类型数组
     * @param index 方法中参数下标，从 0 开始
     * @param isStaticMethod 该方法是否为静态方法
     * @return 访问该方法的 index 位参数的 ASM index
     */
    int getVisitPosition(Type[] types, int index, boolean isStaticMethod) {
        if (types == null || index < 0 || index >= types.length) {
            throw new Error("getVisitPosition error")
        }
        if (index == 0) {
            return isStaticMethod ? 0 : 1
        } else {
            return getVisitPosition(types, index - 1, isStaticMethod) + types[index - 1].getSize()
        }
    }

}

package com.peakmain.analytics.plugin.visitor

import com.peakmain.analytics.plugin.entity.MethodCalledBean
import com.peakmain.analytics.plugin.ext.MonitorConfig
import com.peakmain.analytics.plugin.ext.MonitorHookMethodConfig
import com.peakmain.analytics.plugin.utils.MethodFieldUtils
import com.peakmain.analytics.plugin.visitor.base.MonitorDefalutMethodAdapter
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * author ：Peakmain
 * createTime：2022/4/2
 * mail:2726449200@qq.com
 * describe：
 */
class MonitorMethodCalledReplaceAdapter extends MonitorDefalutMethodAdapter {
    private int mAccess
    private ClassVisitor classVisitor
    private String mClassName
    private MonitorConfig monitorConfig
    private PeakmainVisitor owner
    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param mv @param access the method's access flags (see {@link Opcodes}).
     * @param name the method's name.
     * @param desc
     */
    MonitorMethodCalledReplaceAdapter(MethodVisitor mv, int access, String name, String desc, ClassVisitor classVisitor, String className, MonitorConfig monitorConfig, PeakmainVisitor owner = null) {
        super(mv, access, name, desc)
        mAccess = access
        this.classVisitor = classVisitor
        mClassName = className
        this.monitorConfig = monitorConfig
        this.owner = owner
    }

    @Override
    void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        HashMap<String, MethodCalledBean> methodReplaceBeans = MonitorHookMethodConfig.methodCalledBeans
        String desc = owner + name + descriptor
//        String replacefm = MethodFieldUtils.OWNER_REFLEX_IOC_FM +
//                MethodFieldUtils.NAME_REFLEX_IOC + MethodFieldUtils.DESCRIPTOR_REFLEX_IOC_FM
//
//        String replace = MethodFieldUtils.OWNER_REFLEX_IOC_X + MethodFieldUtils.NAME_REFLEX_IOC +
//                MethodFieldUtils.DESCRIPTOR_REFLEX_IOC_X
//        if (mClassName.contains(MethodFieldUtils.CLASS_REFLEX_LOC) && (replace == desc)) {
//            if (monitorConfig.enableLog) {
//                println("调用反射方法的class:" + mClassName + "调用反射的owner:" + owner + ",方法的名字:" + name + ",方法的描述符：" + descriptor)
//            }
//            super.visitMethodInsn(MethodFieldUtils.STATIC_OPCODE,
//                    MethodFieldUtils.NEW_METHOD_REFLEX_OWNER,
//                    MethodFieldUtils.NAME_REFLEX_IOC,
//                    MethodFieldUtils.DESCRIPTOR_REFLEX_IOC_X,
//                    false)
//        } else if (mClassName.contains(MethodFieldUtils.CLASS_REFLEX_LOC) && replacefm == desc) {
//            if (monitorConfig.enableLog) {
//                println("调用反射方法的class:" + mClassName + "调用反射的owner:" + owner + ",方法的名字:" + name + ",方法的描述符：" + descriptor)
//            }
//            super.visitMethodInsn(MethodFieldUtils.STATIC_OPCODE,
//                    MethodFieldUtils.NEW_METHOD_REFLEX_OWNER,
//                    MethodFieldUtils.NAME_REFLEX_IOC,
//                    MethodFieldUtils.DESCRIPTOR_REFLEX_IOC_FM,
//                    false)
//        } else
        if (!monitorConfig.whiteList.contains(mClassName) && methodReplaceBeans.containsKey(desc)) {
            if (monitorConfig.enableLog) {
                println("调用方法的class:" + mClassName + ",方法的名字:" + name + ",方法的描述符：" + descriptor)
            }
            MethodCalledBean bean = methodReplaceBeans.get(desc)
            super.visitMethodInsn(bean.newOpcode, bean.newMethodOwner, bean.newMethodName, bean.newMethodDescriptor.get(descriptor), false)
            if (owner != null) {
                owner.changed = true
            }
        } else {
            // 原来这里是每个未命中的调用点都无条件 println（会刷屏上百万行、还拖慢构建），
            // 现在只有在 enableLog 打开时才输出。
            if (monitorConfig.enableLog) {
                String deee = ""
                if (mClassName.contains("Welcome") && name.equals(MethodFieldUtils.GET_PERMISSION_METHOD_NAME)) {
                    deee = MethodFieldUtils.PERMISSION_CLASS2 + MethodFieldUtils.GET_PERMISSION_METHOD_NAME + MethodFieldUtils.REQUEST_PERMISSION_DES2
                }
                println("else 调用方法的class:" + mClassName + ",方法的名字:" + name + ",方法的描述符：" + descriptor + " des " + desc + "\n11111111111111  " + deee)
            }
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
        }
    }
}

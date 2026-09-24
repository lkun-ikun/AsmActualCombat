package com.peakmain.analytics.plugin.visitor

import com.peakmain.analytics.plugin.utils.MethodFieldUtils
import com.peakmain.analytics.plugin.utils.OpcodesUtils
import com.peakmain.analytics.plugin.visitor.base.MonitorDefalutMethodAdapter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
/**
 * author ：Peakmain
 * createTime：2022/4/1
 * mail:2726449200@qq.com
 * describe：方法被调用，然后清空被调用的方法
 */
class OvenAdapter extends MonitorDefalutMethodAdapter {
    private String mClassName
    private int mAccess
    private String mDesc

    private String oldOwner = MethodFieldUtils.PERMISSION_CLASS
    private String oldMethod = MethodFieldUtils.GET_PERMISSION_METHOD_NAME
    private String oldDes = MethodFieldUtils.REQUEST_PERMISSION_DES
    private String oldOwner2 = MethodFieldUtils.PERMISSION_CLASS2
    private String oldDes2 = MethodFieldUtils.REQUEST_PERMISSION_DES2
    private String oldOwner3 = MethodFieldUtils.PERMISSION_CLASS3

    /**
     * Constructs a new {@link OvenAdapter}.
     *
     * @param mv
     * @param access the method's access flags (see {@link Opcodes}).
     * @param name the method's name.
     * @param desc
     */
    OvenAdapter(MethodVisitor mv, int access, String name, String desc, String className) {
        super(mv, access, name, desc)
        mClassName = className
        mAccess = access
        mDesc = desc
    }

    @Override
    void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        String des = owner + name + descriptor
        if (des.equals(oldOwner + oldMethod + oldDes)
                || des.equals(oldOwner2 + oldMethod + oldDes2)
                || des.equals(oldOwner3 + oldMethod + oldDes)
        ) {
            println("调用方法的class:" + mClassName + ",方法的名字:" + name + ",方法的描述符：" + descriptor)
            clearMethodBody(mv, mClassName, access, name, descriptor, mDesc)
            return
        }
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
    }


    static void clearMethodBody(MethodVisitor mv, String className, int access, String name, String descriptor, String methodDescriptor) {
        Type type = Type.getType(descriptor)
        Type methodType = Type.getType(methodDescriptor)
        Type methodReturnType = methodType.getReturnType()
        Type[] argumentsType = type.getArgumentTypes()
        Type returnType = type.getReturnType()
        int stackSize = returnType.getSize()
        int localSize = OpcodesUtils.isStatic(access) ? 0 : 1
        for (Type argType : argumentsType) {
            localSize += argType.size
        }
        mv.visitCode()
        if (methodReturnType.getSort() == Type.VOID) {
            mv.visitInsn(RETURN)
        } else if (methodReturnType.getSort() >= Type.BOOLEAN && methodReturnType.getSort() <= Type.INT) {
            mv.visitInsn(ICONST_1)
            mv.visitInsn(IRETURN)
        } else if (returnType.getSort() == Type.LONG) {
            mv.visitInsn(LCONST_0)
            mv.visitInsn(LRETURN)
        } else if (returnType.getSort() == Type.FLOAT) {
            mv.visitInsn(FCONST_0)
            mv.visitInsn(FRETURN)
        } else if (returnType.getSort() == Type.DOUBLE) {
            mv.visitInsn(DCONST_0)
            mv.visitInsn(DRETURN)
        } else if (methodReturnType.getInternalName() == "java/lang/String") {
            mv.visitLdcInsn("")
            mv.visitInsn(ARETURN)
        } else {
            mv.visitInsn(ACONST_NULL)
            mv.visitInsn(ARETURN)
        }
        mv.visitMaxs(stackSize, localSize)
        mv.visitEnd()
    }
}

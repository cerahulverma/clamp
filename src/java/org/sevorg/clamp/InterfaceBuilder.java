package org.sevorg.clamp;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.python.core.BytecodeLoader;

public class InterfaceBuilder
    implements Opcodes
{
    private final String name;

    protected final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    public InterfaceBuilder (String name)
    {
        this.name = name;
        cw.visit(V1_5, getClassAccess(), name, null, "java/lang/Object", null);
    }

    protected int getClassAccess ()
    {
        return ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE;
    }

    public void addMethod (String name, Class<?> returnType, Class<?> params[],
        Class<?> exceptions[])
    {
        String desc = makeMethodDesc(returnType, params);
        String[] excepts = makeExcepts(exceptions);
        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, name, desc, null, excepts).visitEnd();
    }

    public Class<?> load ()
    {
        cw.visitEnd();
        return BytecodeLoader.makeClass(name, cw.toByteArray());
    }

    protected String makeMethodDesc (Class<?> returnType, Class<?>[] params)
    {
        Type[] typeParams = new Type[params.length];
        for (int i = 0; i < typeParams.length; i++) {
            typeParams[i] = Type.getType(params[i]);
        }
        String desc = Type.getMethodDescriptor(Type.getType(returnType), typeParams);
        return desc;
    }

    protected String[] makeExcepts (Class<?>[] exceptions)
    {
        String[] excepts = new String[exceptions.length];
        for (int i = 0; i < exceptions.length; i++) {
            excepts[i] = Type.getInternalName(exceptions[i]);
        }
        return excepts;
    }
}

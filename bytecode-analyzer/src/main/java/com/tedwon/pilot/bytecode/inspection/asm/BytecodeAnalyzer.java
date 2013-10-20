package com.tedwon.pilot.bytecode.inspection.asm;

import org.objectweb.asm.ClassReader;

/**
 * Created by IntelliJ IDEA.
 * User: ted
 * Date: 11. 05. 21
 * Time: PM 9:08
 * To change this template use File | Settings | File Templates.
 */
public class BytecodeAnalyzer {

    public static void main(String[] args) throws Exception {

        ClassPrinter cp = new ClassPrinter();
//        ClassReader cr = new ClassReader("java.lang.Runnable");
        ClassReader cr = new ClassReader("com.tedwon.pilot.bytecode.inspection.asm.TargetClass");
        cr.accept(cp, 8);
    }
}

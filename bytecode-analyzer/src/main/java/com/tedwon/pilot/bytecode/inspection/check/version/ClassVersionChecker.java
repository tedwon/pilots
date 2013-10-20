package com.tedwon.pilot.bytecode.inspection.check.version;

import java.io.*;

/**
 * Description.
 *
 * @author Ted Won
 * @since 1.0
 */
public class ClassVersionChecker {
    public static void main(String[] args) throws IOException {
//        for (int i = 0; i < args.length; i++)
//            checkClassVersion(args[i]);


        checkClassVersion("d:\\ClassVersionChecker.class");
        checkClassVersion("d:\\Appender.class");
        checkClassVersion("d:\\LogFactory.class");


    }

    private static void checkClassVersion(String filename)
        throws IOException {
        DataInputStream in = new DataInputStream
            (new FileInputStream(filename));

        int magic = in.readInt();
        if (magic != 0xcafebabe) {
            System.out.println(filename + " is not a valid class!");
            ;
        }
        int minor = in.readUnsignedShort();
        int major = in.readUnsignedShort();
        System.out.println(filename + ": " + major + "." + minor);
        in.close();
    }
}
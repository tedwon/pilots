package com.tedwon.pilot.bytecode.inspection;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Description.
 * <p/>
 * 1. Load Deprecated API
 * 2. Load Target jar files from directory
 * 3. Read class
 * 4. Search Deprecated API
 * 5. Generate Reports Object
 * 6. Aggregate Reports
 * 7. Generate Final Reports
 *
 * @author Ted Won
 * @since 1.0
 */
public class BytecodeAnalyzer {


    /**
     * Load JDK6 Deprecated API.
     * Return Deprecated API contained Map.
     *
     * @return Deprecated API contained Map
     */
    public Map loadDeprecatedAPI() {

        // Deprecated Interfaces
        List deprecatedInterfaceList = new ArrayList();
        deprecatedInterfaceList.add("org.xml.sax.AttributeList");

        // Deprecated Classes

        Map map = new HashMap();
        map.put(DeprecatedAPITypes.Interfaces, deprecatedInterfaceList);


        return map;

    }

    public void process() {

        /**
         * Read the target directory
         */
        File file = new File("d:\\ted\\data\\libs");

        /**
         * It should be directory.
         */
        if (!file.isDirectory()) {
            return;
        }

        /**
         * Obtain the list of target jar files.
         */
        String[] list = file.list();
        System.out.println(list);

        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {


            /**
             * Identify classes
             */
            try {


                ZipFile jar = new ZipFile(files[i]);
                Enumeration<? extends ZipEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();

                    String className = entry.getName();

                    if (className != null && className.endsWith(".class")) {

                        System.out.println(className);

                        InputStream is = jar.getInputStream(entry);
                        ClassReader cr = new ClassReader(is);
                        ClassNode cn = new ClassNode();
                        cr.accept(cn, ClassReader.EXPAND_FRAMES);

                        System.out.println("Class Name: " + cn.name);
                        System.out.println("Extends: " + cn.superName);
                        System.out.println("Version: " + cn.version);

                        List interlist = cn.interfaces;


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
    }

    public static void main(String[] args) {

        BytecodeAnalyzer ba = new BytecodeAnalyzer();
        ba.process();

    }

}

package com.tedwon.pilot.bytecode.inspection;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Description.
 *
 * @author Ted Won
 * @since 1.0
 */
public class AutomaticBuilder {


    private final File buildFile = new File("d://build." + System.currentTimeMillis() + ".log");
    private final File errorFile = new File("d://build.error." + System.currentTimeMillis() + ".log");
    private final File listFile = new File("d://build.list." + System.currentTimeMillis() + ".log");
    private final File errorlistFile = new File("d://build.error.list." + System.currentTimeMillis() + ".log");

    //    private FileOutputStream fos;
    private PrintWriter buildWriter;
    private PrintWriter errorWriter;
    private PrintWriter listWriter;
    private PrintWriter errorlistWriter;

    public AutomaticBuilder() {
        try {
//            fos = new FileOutputStream(file, true);
            buildWriter = getPrintWriterToAppend(buildFile);
            errorWriter = getPrintWriterToAppend(errorFile);
            listWriter = getPrintWriterToAppend(listFile);
            errorlistWriter = getPrintWriterToAppend(errorlistFile);
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {

        String path = args[0];

        // search under specified directory
        File file = new File(path);

        AutomaticBuilder ab = new AutomaticBuilder();
        ab.process(file);
    }

    public void process(File file) {

        visitAllFiles(file);

        close();
    }

    public void close() {
        IOUtils.closeQuietly(buildWriter);
        IOUtils.closeQuietly(errorWriter);
        IOUtils.closeQuietly(listWriter);
        IOUtils.closeQuietly(errorlistWriter);
    }


    public static Map<String, String> execute(String command, String workingDirectory) {
        CommandLine commandLine = CommandLine.parse(command);
        String cmd = commandLine.toString();
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(workingDirectory));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(out, err);
        executor.setStreamHandler(psh);
        Map<String, String> result = new HashMap();
        try {
            int processExitValue = executor.execute(CommandLine.parse(cmd));
            result.put("exit", "" + processExitValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            result.put("stdout", out.toString());
            result.put("stderr", err.toString());
            result.put("cmd", cmd);
        }
        return result;
    }

    public static void visitAllDirsAndFiles(File dir, FilenameFilter filter) {

        System.out.println(dir.getPath());

        if (dir.isDirectory()) {
            String[] children = dir.list(filter);
            for (int i = 0; i < children.length; i++) {
                visitAllDirsAndFiles(new File(dir, children[i]), filter);
            }
        }
    }

    // Process only directories under dir
    public static void visitAllDirs(File dir) {
        if (dir.isDirectory()) {
            System.out.println(dir.getPath());

            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                visitAllDirs(new File(dir, children[i]));
            }
        }
    }

    // Process only files under dir
    public void visitAllFiles(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                visitAllFiles(new File(dir, children[i]));
            }
        } else {
            if (dir.getPath().endsWith("project.xml")) {
                try {
                    System.out.println(dir.getPath());
                    buildWriter.println(dir.getPath());
                    listWriter.println(dir.getPath());
                    listWriter.flush();
//                    fos.write(dir.getPath().getBytes());
//                    fos.flush();

                    String workingDirectory = dir.getParent();

                    String buildCommand = "D:\\ted\\maven1.0.2\\bin\\maven.bat clean java:compile -Dmaven.compile.deprecation=on";
                    Map<String, String> execute = execute(buildCommand, workingDirectory);

//                    String stdout = execute.get("stdout");
//                    System.out.println(stdout);
//                    buildWriter.println(stdout);
//                    buildWriter.flush();

//                    fos.write(stdout.getBytes());
//                    fos.flush();


                    String stderr = execute.get("stderr");
                    if (stderr.contains(" errors")) {
                        System.out.println(stderr);
                        errorWriter.println(dir.getPath());
                        errorlistWriter.println(dir.getPath());
                        errorlistWriter.flush();
                        errorWriter.println(stderr);
                        errorWriter.flush();
                    }
                } catch (Exception e) {

                }
            }

//            if (dir.getPath().endsWith(".java")) {
//                System.out.println(dir.getPath());
//            }

        }
    }

    public static PrintWriter getPrintWriter(File file) throws FileNotFoundException, UnsupportedEncodingException {
        return getPrintWriter(file, false);
    }


    public static PrintWriter getPrintWriterToAppend(File file) throws FileNotFoundException, UnsupportedEncodingException {
        return getPrintWriter(file, true);
    }

    public static PrintWriter getPrintWriter(File file, boolean append) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, append), "UTF-8"));
        return writer;
    }
}

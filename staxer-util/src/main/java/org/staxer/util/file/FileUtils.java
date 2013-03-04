package org.staxer.util.file;

import org.staxer.util.ResourceUtils;
import org.slf4j.Logger;

import java.io.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 17.04.2008
 * Time: 12:55:21
 */
public class FileUtils {

    public static File createTempFile(String prefix, String suffix, byte[] buffer) throws IOException {
        return createTempFile(null, prefix, suffix, buffer);
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        return createTempFile(null, prefix, suffix, null);
    }

    public static File createTempFile(File dir, String prefix, String suffix, byte[] buffer) throws IOException {
        File tmp = createTempFile(dir, prefix, suffix);
        if (buffer != null) {
            writeToFile(tmp, buffer);
        }
        return tmp;
    }

    public static File createTempFile(File dir, String prefix, String suffix) throws IOException {
        // Create temp file.
        File temp;
        if (dir != null) {
            temp = File.createTempFile(prefix, suffix, dir);
        } else {
            temp = File.createTempFile(prefix, suffix);
        }
        // Delete temp file when program exits.
        temp.deleteOnExit();
        // Write to temp file
        return temp;
    }

    public static void writeToFile(File file, byte[] buffer) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        out.write(buffer);
        out.close();
    }

    public static byte[] getFileData(String fileName) throws IOException {
        if (fileName != null) {
            return getFileData(new File(fileName));
        } else {
            return null;
        }
    }

    public static byte[] getFileData(File file) throws IOException {
        if (file != null && file.exists()) {
            FileInputStream in = new FileInputStream(file);
            byte[] data = ResourceUtils.getStreamData(in);
            in.close();
            return data;
        } else {
            return null;
        }
    }

    public static void createOrCleanupDir(File dir, Logger log) {
        if (!dir.exists()) {
            mkdirs(dir, log);
        } else {
            for (File file : dir.listFiles()) {
                if (!file.isDirectory() && !file.getName().startsWith(".")) {
                    if (!file.delete()) {
                        throw new IllegalStateException("File not deleted: " + file);
                    }
                }
            }
        }
    }

    public static void mkdirs(File dir, Logger log) {
        if (dir.mkdirs()) {
            String message = "Directory created: " + dir.toString();
            if (log != null) {
                log.info(message);
            } else {
                System.out.println(message);
            }
        }
    }
}

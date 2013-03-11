package org.staxer.util.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2013-02-18 12:18
 */
public class FileRotatorWriter {

    public static final int ROTATE_SIZE = 5000;
    private File baseFile;
    private int currentFileIndex;
    private FileWriter currentWriter;
    private boolean closed;
    private int currentEntryIndex = 1;
    private String header;
    private String footer;

    public FileRotatorWriter(File baseFile, int startFileIndex) {
        this.baseFile = baseFile;
        this.currentFileIndex = startFileIndex;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public void write(String s) throws IOException {
        write(s, false);
    }

    public void write(String s, boolean incrementEntryIndex) throws IOException {
        if (!closed && s != null) {
            if (currentWriter == null) {
                createNextFileWriter();
            } else if (currentEntryIndex > ROTATE_SIZE) {
                if (footer != null) {
                    currentWriter.write(footer);
                }
                currentWriter.flush();
                currentWriter.close();
                createNextFileWriter();
                currentEntryIndex = 1;
            }
            currentWriter.write(s);
            if (incrementEntryIndex) {
                currentEntryIndex += 1;
            }
        }
    }

    private void createNextFileWriter() throws IOException {
        currentWriter = new FileWriter(new File(
                baseFile.getParent(),
                String.format("%03d", currentFileIndex)
                        + "-" + baseFile.getName()
        ));
        currentFileIndex += 1;
        if (header != null) {
            currentWriter.write(header);
        }
    }

    public void close() throws IOException {
        if (!closed ) {
            if (currentWriter != null) {
                if (footer != null) {
                    currentWriter.write(footer);
                }
                currentWriter.flush();
                currentWriter.close();
            }
            closed = true;
        }
    }

}

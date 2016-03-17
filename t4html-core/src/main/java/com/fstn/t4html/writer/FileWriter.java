package com.fstn.t4html.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by SZA on 14/03/2016.
 * Utils that provide easy way to write inside file
 */
public class FileWriter {
    /**
     * Write into path file
     * @param fileContent
     * @param filePath
     * @throws IOException
     */
    public static void write(StringBuffer fileContent, Path filePath) throws IOException {
        FileOutputStream os = new FileOutputStream(filePath.toFile());
        os.write(fileContent.toString().getBytes());
        os.close();
    }
}

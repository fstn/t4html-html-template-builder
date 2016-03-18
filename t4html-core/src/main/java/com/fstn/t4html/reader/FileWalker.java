package com.fstn.t4html.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;


/**
 * Created by stephen on 18/03/2016.
 * Read file content and callVisitor
 */
public class FileWalker {
    private static Logger logger = LoggerFactory.getLogger(FileWalker.class.getName());
    private String pathFrom;
    private SimpleFileVisitor<Path> visitor;
    /**
     * Read
     * @return
     */
    public static FileWalker read() {
        return new FileWalker();
    }

    /**
     * Select from folder
     * @param pathFrom
     * @return
     */
    public FileWalker from(String pathFrom) {
        this.pathFrom = pathFrom;
        return this;
    }

    /**
     * Set visitor
     * @param visitor
     * @return
     */
    public FileWalker visitor(SimpleFileVisitor<Path> visitor) {
        this.visitor = visitor;
        return this;
    }

    /**
     * Parse Directory to extract files that match with content
     * @return
     */
    public void execute() {
        try {
            Files.walkFileTree(Paths.get(pathFrom), visitor);
        } catch (IOException e) {
            logger.error("Unable to walk inside path "+pathFrom,e);
        }
    }
}
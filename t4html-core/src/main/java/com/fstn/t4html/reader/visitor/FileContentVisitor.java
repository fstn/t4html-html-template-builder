package com.fstn.t4html.reader.visitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Collectors;

/**
 * Created by stephen on 18/03/2016.
 * Filter file by matcher, read file content and add it inside stringBuffer
 */
public class FileContentVisitor extends SimpleFileVisitor<Path>{
    private static Logger logger = LoggerFactory.getLogger(FileContentVisitor.class.getName());

    private PathMatcher matcher;
    private StringBuffer fileContent;

    public FileContentVisitor(String expressionMatcher, StringBuffer fileContent) {
        this.matcher = FileSystems.getDefault().getPathMatcher(expressionMatcher);
        this.fileContent = fileContent;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (matcher.matches(path)) {
            logger.info(path.toString()+": Looking for blocks ");
            logger.debug("Files Content: " + fileContent);
            fileContent.append(Files.lines(path)
                    .collect(Collectors.joining("\n")));
        }else{
            logger.info(path.toString()+": Ignoring file ");
        }
        return  FileVisitResult.CONTINUE;
    }


}

package com.fstn.t4html.applier.visitor;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import com.fstn.t4html.applier.BlockApplier;
import com.fstn.t4html.writer.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by stephen on 18/03/2016.
 * Visit all final files and apply
 */
public class TemplateApplierVisitor extends SimpleFileVisitor<Path> {
    private Logger logger = LoggerFactory.getLogger(SimpleFileVisitor.class.getName());

    private PathMatcher matcher;
    private Pattern blockPattern;
    private List<Block> modulesBlock;

    public TemplateApplierVisitor(List<Block> modulesBlock, String expressionMatcher) {
        this.matcher = FileSystems.getDefault().getPathMatcher(expressionMatcher);
        this.blockPattern = Pattern.compile(Config.START_FLAG + ":" + "([^:]*):([^:]*)-->");
        this.modulesBlock = modulesBlock;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (matcher.matches(path)) {
            try {
                String fileContentResult = Files.lines(path).collect(Collectors.joining("\n"));

                if (fileContentResult != null) {
                    Matcher m = blockPattern.matcher(fileContentResult);
                    logger.debug(path.toString() + ": File content: " + fileContentResult);

                    StringBuffer fileContentResultBuffer = new StringBuffer(fileContentResult);
                    while (m.find()) {
                        String verb = m.group(1).trim();
                        if (verb.equals(Config.DESCRIBE_VERB)) {

                            //Apply block that are the same name
                            String name = m.group(2).trim();
                            String startTag = Config.START_FLAG + ":" + verb + ":" + name + "-->";
                            String endTag = Config.END_FLAG + ":" + verb + ":" + name + "-->";
                            logger.debug(path.toString() + ": Looking for betweenTags: " + startTag + " " + endTag);

                            modulesBlock.stream()
                                    .filter(block -> block.getName().equals(name))
                                    .collect(Collectors.toList())
                                    .forEach(block -> {
                                        BlockApplier.read().betweenTags(startTag, endTag).from(path).apply(block).inside(fileContentResultBuffer).apply();
                                    });

                            logger.debug(path.toString() + ": Result " + fileContentResultBuffer.toString());
                        }
                    }
                    FileWriter.write(fileContentResultBuffer, path);
                    return FileVisitResult.CONTINUE;
                } else {
                    logger.error(path.toString() + ": File content is empty " + path.toString());
                }
            } catch (IOException e) {
                logger.error("Unable to write blocks inside file ", e);
                throw new RuntimeException(e);
            }
        } else {
            logger.debug(path.toString() + ": Ignoring file ");
        }
        return FileVisitResult.CONTINUE;
    }

}

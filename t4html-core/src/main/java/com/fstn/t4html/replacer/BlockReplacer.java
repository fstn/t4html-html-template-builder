package com.fstn.t4html.replacer;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import com.fstn.t4html.writer.FileWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by stephen on 13/03/2016.
 * Class that replace original blocks with modules blocks
 */
public class BlockReplacer {
    private Logger logger = LoggerFactory.getLogger(BlockReplacer.class.getName());
    private String pathFrom;
    private String pathTo;
    private String extension;


    public static BlockReplacer read() {
        return new BlockReplacer();
    }


    /**
     * Path that contains block
     *
     * @param extension
     * @return
     */
    public BlockReplacer fileEndsWith(String extension) {
        this.extension = extension;
        return this;
    }

    /**
     * Path that contains block
     *
     * @param pathFrom
     * @return
     */
    public BlockReplacer from(String pathFrom) {
        this.pathFrom = pathFrom;
        return this;
    }

    /**
     * Path that contains block
     *
     * @param pathTo
     * @return
     */
    public BlockReplacer to(String pathTo) {
        this.pathTo = pathTo;
        return this;
    }

    /**
     * Replace file content with block module
     *
     * @param modulesBlock
     * @return
     */
    public void replace(List<Block> modulesBlock) throws IOException {
        Pattern blockPattern = Pattern.compile(Config.START_FLAG + ":" + "([^:]*):([^:]*)-->");
        FileUtils.copyDirectory(new File(pathFrom), new File(pathTo));

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*" + extension + "");
        logger.debug("Replace file with extension: " + extension);

        Files.walkFileTree(Paths.get(pathTo), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (matcher.matches(path)) {
                    try {
                        String fileContentResult = Files.lines(path).collect(Collectors.joining("\n"));

                        Matcher m = blockPattern.matcher(fileContentResult);
                        logger.debug(path.toString() + ": File content: " + fileContentResult);

                        StringBuffer fileContentResultBuffer = new StringBuffer(fileContentResult);
                        while (m.find()) {
                            String verb = m.group(1).trim();
                            String name = m.group(2).trim();
                            String startTag = Config.START_FLAG + ":" + verb + ":" + name + "-->";
                            String endTag = Config.END_FLAG + ":" + verb + ":" + name + "-->";
                            logger.debug(path.toString() + ": Looking for tags: " + startTag + " " + endTag);
                            if (fileContentResult != null) {
                                modulesBlock.stream()
                                        .filter(block -> block.getName().equals(name))
                                        .collect(Collectors.toList())
                                        .forEach(block -> {
                                            logger.info(path.toString() + ": tag found: " + startTag + " " + endTag);
                                            int startTagIndex = fileContentResultBuffer.indexOf(startTag);
                                            int endTagIndex = fileContentResultBuffer.indexOf(endTag);
                                            int contentStartIndex = startTagIndex + startTag.length();
                                            int contentEndIndex = endTagIndex - 1;
                                            int endOfEndTagIndex = endTagIndex + endTag.length();
                                            if (contentEndIndex < 0 || contentStartIndex < 0) {
                                                logger.error("Unable to find end or start of tags " + startTag + " inside " + fileContentResultBuffer.toString());
                                            } else {
                                                switch (block.getVerb()) {
                                                    //insert inside parent just before end tag
                                                    case Config.APPEND_VERB:
                                                        logger.info(path.toString() + ": Add element " + block + " after tag: " + startTag);
                                                        fileContentResultBuffer.replace(contentEndIndex, contentEndIndex, block.getContentWithTags());
                                                        break;
                                                    //insert inside parent just after start tag
                                                    case Config.PREPEND_VERB:
                                                        logger.info(path.toString() + ": Add element " + block + " before tag: " + startTag + " inside " + path.toString());
                                                        fileContentResultBuffer.replace(contentStartIndex, contentStartIndex, block.getContentWithTags());
                                                        break;
                                                    //insert outside parent just after end block
                                                    case Config.AFTER_VERB:
                                                        logger.info(path.toString() + ": Add element " + block + " after tag: " + startTag);
                                                        fileContentResultBuffer.replace(endOfEndTagIndex, endOfEndTagIndex, block.getContentWithTags());
                                                        break;
                                                    //insert outside parent just before start tag
                                                    case Config.BEFORE_VERB:
                                                        logger.info(path.toString() + ": Add element " + block + " before tag: " + startTag + " inside " + path.toString());
                                                        fileContentResultBuffer.replace(contentStartIndex, contentStartIndex, block.getContentWithTags());
                                                        break;
                                                    //replace block parent content
                                                    case Config.REPLACE_VERB:
                                                        logger.info(path.toString() + ": Replace with element " + block + " inside tag: " + startTag + " inside " + path.toString());
                                                        fileContentResultBuffer.replace(startTagIndex, endOfEndTagIndex, block.getContentWithTags());
                                                        break;
                                                    //insert around block
                                                    case Config.AROUND_VERB:
                                                        logger.info(path.toString() + ": Around element " + block + " inside tag: " + startTag + " inside " + path.toString());

                                                        String[] originalContentArray = StringUtils.substringsBetween(fileContentResultBuffer.toString(), startTag, endTag);
                                                        fileContentResultBuffer.replace(contentStartIndex, contentEndIndex, block.getContentWithTags());
                                                        //Replacing insert flag with original content
                                                        if (originalContentArray.length > 0) {
                                                            int insertContentStartIndex = fileContentResultBuffer.indexOf(Config.INSERT_FLAG);
                                                            int insertContentEndIndex = insertContentStartIndex + Config.INSERT_FLAG.length();
                                                            fileContentResultBuffer.replace(insertContentStartIndex, insertContentEndIndex, originalContentArray[0]);

                                                        }

                                                        break;
                                                    //do nothing with block
                                                    case Config.DESCRIBE_VERB:
                                                        logger.info(path.toString() + ": Nothing to do with " + block);
                                                        break;
                                                    default:
                                                        throw new UnsupportedOperationException("Unable to execute action " + block.getVerb());
                                                }
                                            }
                                        });
                            } else {
                                logger.error(path.toString() + ": File content is empty " + path.toString());
                            }
                            logger.debug(path.toString() + ": Result " + fileContentResultBuffer.toString());

                        }
                        FileWriter.write(fileContentResultBuffer, path);
                        return FileVisitResult.CONTINUE;
                    } catch (IOException e) {
                        logger.error("Unable to write blocks inside file ", e);
                        throw new RuntimeException(e);
                    }
                } else {
                    logger.debug(path.toString() + ": Ignoring file ");
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

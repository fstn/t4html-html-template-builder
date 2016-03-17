package com.fstn.t4html.parser;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by stephen on 11/03/2016.
 */
public class BlockParser {

    private String pathFrom;
    private Logger logger = LoggerFactory.getLogger(BlockParser.class.getName());
    private String extension;

    public static BlockParser read() {
        return new BlockParser();
    }

    /**
     * Path that contains block
     * @param pathFrom
     * @return
     */
    public BlockParser from(String pathFrom) {
        this.pathFrom = pathFrom;
        return this;
    }

    /**
     * Path that contains block
     * @param extension
     * @return
     */
    public BlockParser fileEndsWith(String extension) {
        this.extension = extension;
        return this;
    }



    /**
     * Parse Directory to extract block from blocks files
     * @return
     * @throws IOException
     */
    public List<Block> parse() throws IOException {
        List<Block> blocks = new ArrayList<>();
        Pattern blockPattern = Pattern.compile(Config.START_FLAG + ":" + "([^:]*):([^:]*)-->");

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*" + extension + "");
        logger.debug("Replace file with extension: " + extension);

        final StringBuffer allBlocksString = new StringBuffer();
        Files.walkFileTree(Paths.get(pathFrom), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        if (matcher.matches(path)) {
                            logger.info(path.toString()+": Looking for blocks ");
                            logger.debug("Files Content: " + allBlocksString);
                            allBlocksString.append(Files.lines(path)
                                    .collect(Collectors.joining("\n")));
                        }else{
                            logger.info(path.toString()+": Ignoring file ");
                        }
                        return  FileVisitResult.CONTINUE;
                    }
                });
        Matcher m = blockPattern.matcher(allBlocksString);
        while (m.find()) {
            int startIndex = m.start();
            String verb = m.group(1).trim();
            String name = m.group(2).trim();
            String startTag = Config.START_FLAG + ":" + verb + ":" + name + "-->";
            String endTag = Config.END_FLAG + ":" + verb + ":" + name + "-->";
            logger.info("Looking for tags: " + startTag + " " + endTag);
            if(!verb.equals(Config.DESCRIBE_VERB)) {
                if (allBlocksString != null) {
                    //remove String that are before current match in order to retreat another block already done
                    String splitResult = allBlocksString.toString().substring(startIndex);
                    String[] contentAsArray = StringUtils.substringsBetween(splitResult, startTag, endTag);
                    if (contentAsArray != null && contentAsArray.length > 0) {
                        String content = contentAsArray[0];
                        Block block = new Block(name, verb, content);
                        logger.debug("Find block " + block);
                        if (!blocks.contains(block)) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        logger.debug("Blocks Result: " + blocks);
        return blocks;
    }
}

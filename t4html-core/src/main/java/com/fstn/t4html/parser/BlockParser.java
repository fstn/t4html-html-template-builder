package com.fstn.t4html.parser;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        String allBlocksString = Files.list(Paths.get(pathFrom))
                .map(String::valueOf)
                .filter(pathString -> pathString.endsWith(extension))
                .sorted()
                .map(pathString -> Paths.get(pathString))
                .map(path -> {
                    try {
                        return Files.lines(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(l -> l)
                .collect(Collectors.joining("\n"));

        Matcher m = blockPattern.matcher(allBlocksString);
        logger.debug("Files Content: " + allBlocksString);
        while (m.find()) {
            String verb = m.group(1).trim();
            String name = m.group(2).trim();
            String startTag = Config.START_FLAG + ":" + verb + ":" + name + "-->";
            String endTag = Config.END_FLAG + ":" + verb + ":" + name + "-->";
            logger.info("Looking for tags: " + startTag + " " + endTag);
            if(allBlocksString != null) {
                String[] contentAsArray = StringUtils.substringsBetween(allBlocksString, startTag, endTag);
                if(contentAsArray != null && contentAsArray.length > 0) {
                    String content = contentAsArray[0];
                    Block block = new Block( name, verb, content);
                    logger.debug("Find block " + block);
                    blocks.add(block);
                }
            }
        }
        logger.debug("Blocks " + blocks);
        return blocks;
    }
}

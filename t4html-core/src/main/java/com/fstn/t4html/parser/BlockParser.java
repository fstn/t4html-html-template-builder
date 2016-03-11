package com.fstn.t4html.parser;

import com.fstn.t4html.model.Block;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by stephen on 11/03/2016.
 */
public class BlockParser {

    private final String START_FLAG = "<!--start-block";
    private final String END_FLAG = "<!--end-block";
    private final String BLOCK_EXTENSION = ".blocks.html";
    private String pathFrom;
    private Logger logger = Logger.getLogger(BlockParser.class.getName());

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
     * Parse Directory to extract block from blocks files
     * @return
     * @throws IOException
     */
    public List<Block> parse() throws IOException {
        List<Block> blocks = new ArrayList<>();
        Pattern blockPattern = Pattern.compile(START_FLAG + ":" + "([^:]*):([^:]*)-->");

        String allBlocksString = Files.list(Paths.get(pathFrom))
                .map(String::valueOf)
                .filter(pathString -> pathString.contains(BLOCK_EXTENSION))
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
                .collect(Collectors.joining(""));

        Matcher m = blockPattern.matcher(allBlocksString);
        logger.log(Level.INFO, "AllBlocks: " + allBlocksString);
        while (m.find()) {
            String verb = m.group(1).trim();
            String name = m.group(2).trim();
            String startTag = START_FLAG + ":" + verb + ":" + name + "-->";
            String endTag = END_FLAG + ":" + verb + ":" + name + "-->";
            logger.log(Level.INFO, "Looking for tags: " + startTag + " " + endTag);
            String content = StringUtils.substringsBetween(allBlocksString, startTag, endTag)[0];
            Block block = new Block(name, verb, content);
            logger.log(Level.INFO, "Find block " + block);
            blocks.add(block);
        }
        logger.log(Level.INFO, "Blocks " + blocks);
        return blocks;
    }
}

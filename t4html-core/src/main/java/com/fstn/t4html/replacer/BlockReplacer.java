package com.fstn.t4html.replacer;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import com.fstn.t4html.parser.BlockParser;
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
 * Created by stephen on 13/03/2016.
 * Class that replace original blocks with modules blocks
 */
public class BlockReplacer {
    private Logger logger = Logger.getLogger(BlockParser.class.getName());
    private String pathFrom;
    private String extension;


    public static BlockReplacer read() {
        return new BlockReplacer();
    }


    /**
     * Path that contains block
     * @param extension
     * @return
     */
    public BlockReplacer fileEndsWith(String extension) {
        this.extension = extension;
        return this;
    }

    /**
     * Path that contains block
     * @param pathFrom
     * @return
     */
    public BlockReplacer from(String pathFrom) {
        this.pathFrom = pathFrom;
        return this;
    }


    /**
     * Replace file content with block module
     * @param modulesBlock
     * @return
     */
    public BlockReplacer replace (List<Block> modulesBlock) throws IOException {
        List<Block> blocks = new ArrayList<>();
        Pattern blockPattern = Pattern.compile(Config.START_FLAG + ":" + "([^:]*):([^:]*)-->");

        String fileContentResult = Files.list(Paths.get(pathFrom))
                .map(String::valueOf)
                .filter(pathString -> pathString.contains(extension))
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

        Matcher m = blockPattern.matcher(fileContentResult);
        logger.log(Level.INFO, "AllBlocks: " + fileContentResult);
        while (m.find()) {
            String verb = m.group(1).trim();
            String name = m.group(2).trim();
            String startTag = Config.START_FLAG + ":" + verb + ":" + name + "-->";
            String endTag = Config.END_FLAG + ":" + verb + ":" + name + "-->";
            logger.log(Level.INFO, "Looking for tags: " + startTag + " " + endTag);
            if(fileContentResult != null) {
                String[] contentAsArray = StringUtils.substringsBetween(fileContentResult, startTag, endTag);
                if(contentAsArray != null && contentAsArray.length > 0) {
                    String content = contentAsArray[0];
                    Block block = new Block(name, verb, content);
                    logger.log(Level.INFO, "Find block " + block);
                    blocks.add(block);
                }
            }
        }
        logger.log(Level.INFO, "Blocks " + blocks);
        return this;
    }
}

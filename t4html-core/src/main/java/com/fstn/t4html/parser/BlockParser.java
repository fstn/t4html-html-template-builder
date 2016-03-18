package com.fstn.t4html.parser;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import com.fstn.t4html.reader.FileWalker;
import com.fstn.t4html.reader.visitor.FileContentVisitor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     *
     * @param pathFrom
     * @return
     */
    public BlockParser from(String pathFrom) {
        this.pathFrom = pathFrom;
        return this;
    }

    /**
     * Path that contains block
     *
     * @param extension
     * @return
     */
    public BlockParser fileEndsWith(String extension) {
        this.extension = extension;
        return this;
    }


    /**
     * Parse Directory to extract block from blocks files
     *
     * @return
     * @throws IOException
     */
    public List<Block> parse() throws IOException {
        List<Block> blocks = new ArrayList<>();

        logger.debug("Replace file with extension: " + extension);

        StringBuffer allBlocksString = new StringBuffer();
        FileWalker
                .read()
                .from(pathFrom)
                .visitor(new FileContentVisitor("glob:**/*" + extension + "", allBlocksString))
                .execute();

        Pattern blockPattern = Pattern.compile(Config.START_FLAG + ":" + "([^:]*):([^:]*)-->");
        Matcher m = blockPattern.matcher(allBlocksString);

        while (m.find()) {
            int startIndex = m.start();
            String verb = m.group(1).trim();
            String name = m.group(2).trim();
            String startTag = Config.START_FLAG + ":" + verb + ":" + name + "-->";
            String endTag = Config.END_FLAG + ":" + verb + ":" + name + "-->";
            logger.info("Looking for betweenTags: " + startTag + " " + endTag);
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
        logger.debug("Blocks Result: " + blocks);
        return blocks;
    }
}

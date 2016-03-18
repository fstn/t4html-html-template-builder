package com.fstn.t4html.applier;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Created by stephen on 18/03/2016.
 * Class that apply block on StringBuffer
 */
public class BlockApplier {
    private Path pathFrom;
    private Block block;
    private StringBuffer fileContentResultBuffer;
    private Logger logger = LoggerFactory.getLogger(BlockApplier.class.getName());
    private String startTag;
    private String endTag;


    public static BlockApplier read() {
        return new BlockApplier();
    }

    public BlockApplier from(Path pathFrom) {
        this.pathFrom = pathFrom;
        return this;
    }

    public BlockApplier apply(Block block) {
        this.block = block;
        return this;
    }

    public BlockApplier inside(StringBuffer fileContentResultBuffer) {
        this.fileContentResultBuffer = fileContentResultBuffer;
        return this;
    }


    public BlockApplier betweenTags(String startTag, String endTag) {
        this.startTag = startTag;
        this.endTag = endTag;
        return this;
    }

    public void apply() {
        String verb = block.getVerb();
        logger.info(pathFrom.toString() + ": tag found: " + startTag + " " + endTag);
        int startTagIndex = fileContentResultBuffer.indexOf(startTag);
        int endTagIndex = fileContentResultBuffer.indexOf(endTag);
        int contentStartIndex = startTagIndex + startTag.length();
        int contentEndIndex = endTagIndex - 1;
        int endOfEndTagIndex = endTagIndex + endTag.length();
        if (contentEndIndex < 0 || contentStartIndex < 0) {
            logger.error("Unable to find end or start of betweenTags " + startTag + " inside " + fileContentResultBuffer.toString());
        } else {
            switch (verb) {
                //insert inside parent just before end tag
                case Config.APPEND_VERB:
                    logger.info(pathFrom.toString() + ": Add element " + block + " after tag: " + startTag);
                    fileContentResultBuffer.insert(contentEndIndex, block.getContent());
                    break;
                //insert inside parent just after start tag
                case Config.PREPEND_VERB:
                    logger.info(pathFrom.toString() + ": Add element " + block + " before tag: " + startTag + " inside " + pathFrom.toString());
                    fileContentResultBuffer.insert(contentStartIndex, block.getContent());
                    break;
                //insert outside parent just after end block
                case Config.AFTER_VERB:
                    logger.info(pathFrom.toString() + ": Add element " + block + " after tag: " + startTag);
                    fileContentResultBuffer.insert(endOfEndTagIndex, block.getContent());
                    break;
                //insert outside parent just before start tag
                case Config.BEFORE_VERB:
                    logger.info(pathFrom.toString() + ": Add element " + block + " before tag: " + startTag + " inside " + pathFrom.toString());
                    fileContentResultBuffer.insert(startTagIndex, block.getContent());
                    break;
                //apply block parent content
                case Config.REPLACE_VERB:
                    logger.info(pathFrom.toString() + ": Replace with element " + block + " inside tag: " + startTag + " inside " + pathFrom.toString());
                    fileContentResultBuffer.replace(startTagIndex, endOfEndTagIndex, block.getContent());
                    break;
                //insert around block
                case Config.AROUND_VERB:
                    logger.info(pathFrom.toString() + ": Around element " + block + " inside tag: " + startTag + " inside " + pathFrom.toString());

                    String[] originalContentArray = StringUtils.substringsBetween(fileContentResultBuffer.toString(), startTag, endTag);
                    fileContentResultBuffer.replace(contentStartIndex, contentEndIndex, block.getContentWithTags());
                    //Replacing insert flag with original content
                    if (originalContentArray.length > 0) {
                        int insertContentStartIndex = fileContentResultBuffer.indexOf(Config.INSERT_FLAG);
                        if (insertContentStartIndex < 0) {
                            throw new RuntimeException("You need to append " + Config.INSERT_FLAG + " between " + startTag
                                    + " and " + endTag + " " + " to work with " + verb+" tag");
                        }
                        int insertContentEndIndex = insertContentStartIndex + Config.INSERT_FLAG.length();
                        fileContentResultBuffer.replace(insertContentStartIndex, insertContentEndIndex, originalContentArray[0]);


                    }

                    break;
                //do nothing with block
                case Config.DESCRIBE_VERB:
                    logger.info(pathFrom.toString() + ": Nothing to do with " + block);
                    break;
                default:
                    throw new UnsupportedOperationException("Unable to apply action " + block.getVerb());
            }
        }
    }
}

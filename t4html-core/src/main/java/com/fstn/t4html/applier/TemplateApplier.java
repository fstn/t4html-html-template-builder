package com.fstn.t4html.applier;

import com.fstn.t4html.model.Block;
import com.fstn.t4html.applier.visitor.TemplateApplierVisitor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by stephen on 13/03/2016.
 * Class that apply original blocks with modules blocks
 */
public class TemplateApplier {
    private Logger logger = LoggerFactory.getLogger(TemplateApplier.class.getName());
    private String pathFrom;
    private String pathTo;
    private String extension;


    public static TemplateApplier read() {
        return new TemplateApplier();
    }


    /**
     * Path that contains block
     *
     * @param extension
     * @return
     */
    public TemplateApplier fileEndsWith(String extension) {
        this.extension = extension;
        return this;
    }

    /**
     * Path that contains block
     *
     * @param pathFrom
     * @return
     */
    public TemplateApplier from(String pathFrom) {
        this.pathFrom = pathFrom;
        return this;
    }

    /**
     * Path that contains block
     *
     * @param pathTo
     * @return
     */
    public TemplateApplier to(String pathTo) {
        this.pathTo = pathTo;
        return this;
    }

    /**
     * Replace file content with block module
     *
     * @param modulesBlock
     * @return
     */
    public void apply(List<Block> modulesBlock) throws IOException {
        FileUtils.copyDirectory(new File(pathFrom), new File(pathTo));
        logger.debug("Replace file with extension: " + extension);
        Files.walkFileTree(Paths.get(pathTo), new TemplateApplierVisitor(modulesBlock,"glob:**/*" + extension + ""));
    }
}

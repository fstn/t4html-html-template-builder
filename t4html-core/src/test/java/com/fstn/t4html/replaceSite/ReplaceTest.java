package com.fstn.t4html.replaceSite;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import com.fstn.t4html.parser.BlockParser;
import com.fstn.t4html.replacer.BlockReplacer;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by stephen on 14/03/2016.
 */
public class ReplaceTest {

    String fromFolder = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "replaceSite";
    String toFolder = "src" + File.separator + "test-result" + File.separator + "resources" + File.separator + "replaceSite";

    @Test
    public void simpleModuleCase() {

        String expectedBlocksResult = "\n" +
                "<!--start-block:describe:header-tva-fr-net-->\n" +
                "<div class='col-md-2' ng-controller=\"netAmountController\">\n" +
                "    <div class='form-group'>\n" +
                "        <div class='form-control-wrapper'>\n" +
                "            TVA net amount\n" +
                "            <input it-input\n" +
                "                   class='form-control'\n" +
                "                   type='text'\n" +
                "                   name='tvaFR'\n" +
                "                   required=''\n" +
                "                   it-label='TVA FR'\n" +
                "                   ng-model='invoice.data.custom.tva'/>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<!--end-block:describe:header-tva-fr-net-->";
        try {

            FileUtils.deleteDirectory(new File(toFolder));

            List<Block> blocks = BlockParser.read().fileEndsWith(Config.BLOCK_EXTENSION).from(fromFolder).parse();
            //Assert.assertEquals("incorrect block parsing", expectedBlocksResult, blocks.toString());
            BlockReplacer
                    .read()
                    .to(toFolder)
                    .from(fromFolder)
                    .fileEndsWith(".html")
                    .replace(blocks);

            String finalViewContent = Files.lines(Paths.get(toFolder + File.separator + "html" + File.separator
                    + "app" + File.separator + "view.html")).collect(Collectors.joining("\n"));

            Assert.assertEquals("replace with block",expectedBlocksResult, finalViewContent);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage() + ":" + new File(".").getAbsolutePath());
        }
    }
}

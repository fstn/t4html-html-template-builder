package com.fstn.t4html;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import com.fstn.t4html.parser.BlockParser;
import com.fstn.t4html.replacer.BlockReplacer;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by stephen on 14/03/2016.
 */
public class BlockReplacerTest {

    @Test
    public void simpleModuleCase() {

        String expectedBlocksResult = "[Block{name='content', verb='replace', content='My html EU content'}, Block{name='content', verb='append', content='My html FR content    <!--start-block:append:contentInvoice-->    My Invoice content    <!--end-block:append:contentInvoice-->'}, Block{name='contentInvoice', verb='append', content='    My Invoice content    '}]";
        String expectedOriginalResult = "<!DOCTYPE html><html lang=\"en\"><head>    <meta charset=\"UTF-8\">    <title>Title</title>    <div>    <!--start-block:describe:content--><!--start-block:replace:content-->My html EU content<!--end-block:replace:content--><!--start-block:append:content-->My html FR content    <!--start-block:append:contentInvoice-->    My Invoice content    <!--end-block:append:contentInvoice--><!--end-block:append:content--> <!--end-block:describe:content-->    </div></head><body></body></html>";

        try {

            List<Block> blocks = BlockParser.read().fileEndsWith(Config.BLOCK_EXTENSION).from("src\\test\\resources\\html\\blocks").parse();
            Assert.assertEquals("incorrect block parsing", expectedBlocksResult, blocks.toString());
            String replaceResult = BlockReplacer.read().from("src\\test\\resources\\html").fileEndsWith(".html").replace(blocks);
            Assert.assertEquals("incorrect block replacing", expectedOriginalResult, replaceResult);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage()+""+new File(".").getPath());
        }
    }
}

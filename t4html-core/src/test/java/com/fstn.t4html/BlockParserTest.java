package com.fstn.t4html;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import com.fstn.t4html.parser.BlockParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by stephen on 13/03/2016.
 */
public class BlockParserTest {

    @Test
    public void simpleModuleCase(){

        String expectedResult = "[Block{name='content', verb='replace', content='My html EU content'}, Block{name='content', verb='append', content='My html FR content    <!--start-block:append:contentInvoice-->    My Invoice content    <!--end-block:append:contentInvoice-->'}, Block{name='contentInvoice', verb='append', content='    My Invoice content    '}]";

        try {
            List<Block> realResult = BlockParser.read().fileEndsWith(Config.BLOCK_EXTENSION).from("src"+File.pathSeparator+"test"+File.pathSeparator+"resources"+File.pathSeparator+"html"+File.pathSeparator+"blocks").parse();
            Assert.assertEquals("incorrect block parsing",expectedResult,realResult.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void simpleOriginalCase(){

        String expectedResult = "[Block{name='content', verb='describe', content='        My html content    '}]";

        try {
            List<Block> realResult = BlockParser.read()
                    .fileEndsWith(".html")
                    .from("src"+File.pathSeparator+"test"+File.pathSeparator+"resources"+File.pathSeparator+"html")
                    .parse();
            Assert.assertEquals("incorrect block parsing",expectedResult,realResult.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}

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

        String expectedBlocksResult = "[Block{name='content', verb='replace', content='\n" +
                "My html EU content\n" +
                "'}, Block{name='content', verb='after', content='\n" +
                "My html FR content\n" +
                "    <!--start-block:after:contentInvoice-->\n" +
                "    My Invoice content\n" +
                "    <!--end-block:after:contentInvoice-->\n" +
                "'}, Block{name='contentInvoice', verb='after', content='\n" +
                "    My Invoice content\n" +
                "    '}, Block{name='header-total-amount', verb='after', content='\n" +
                "\n" +
                "<div class='col-md-2'>\n" +
                "    <div class='form-group'>\n" +
                "        <div class='form-control-wrapper'>\n" +
                "            <input it-input\n" +
                "                   class='form-control floating-label'\n" +
                "                   type='text'\n" +
                "                   name='tvaFR'\n" +
                "                   required=''\n" +
                "                   it-label='TVA FR'\n" +
                "                   ng-model='invoiceData.header.tvaFR'/>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "'}]";
         try {

            List<Block> blocks = BlockParser.read().fileEndsWith(Config.BLOCK_EXTENSION).from("../t4html-quickstart-case"+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+ "quickstart"+File.separator+"angular-common-quickstart"+File.separator+"fr").parse();
            //Assert.assertEquals("incorrect block parsing", expectedBlocksResult, blocks.toString());
            BlockReplacer
                    .read()
                    .to("target"+File.separator+"test"+File.separator+"resources"+File.separator+ "fullSite"+File.separator+"html")
                    .from("../t4html-quickstart-case"+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+ "quickstart"+File.separator+"angular-common-quickstart"+File.separator+"main" +File.separator)
                    .fileEndsWith(".html")
                    .replace(blocks);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage()+":"+new File(".").getAbsolutePath());
        }
    }
}

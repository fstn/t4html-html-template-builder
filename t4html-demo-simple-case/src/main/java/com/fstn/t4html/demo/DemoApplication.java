package com.fstn.t4html.demo;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import com.fstn.t4html.parser.BlockParser;

import java.io.IOException;
import java.util.List;

/**
 * Created by stephen on 11/03/2016.
 */
public class DemoApplication {
    public static void main(String [] args) {
        try {
            List<Block> moduleBlocks =  BlockParser.read()
                    .from("t4html-demo-simple-case\\src\\main\\resources\\html\\blocks")
                    .fileEndsWith(Config.BLOCK_EXTENSION)
                    .parse();

            List<Block> originalBlocks = BlockParser.read()
                    .from("t4html-demo-simple-case\\src\\main\\resources\\html")
                    .fileEndsWith(".html")
                    .parse();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

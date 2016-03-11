package com.fstn.t4html.demo;

import com.fstn.t4html.parser.BlockParser;

import java.io.IOException;

/**
 * Created by stephen on 11/03/2016.
 */
public class DemoApplication {
    public static void main(String [] args) {
        try {
            BlockParser.read().from("C:\\DevTools\\Git\\t4html-html-template-builder\\t4html-demo-simple-case\\src\\main\\resources\\html\\blocks").parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

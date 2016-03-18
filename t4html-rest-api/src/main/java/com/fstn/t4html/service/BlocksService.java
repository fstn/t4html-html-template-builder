package com.fstn.t4html.service;

import com.fstn.t4html.config.Config;
import com.fstn.t4html.model.Block;
import com.fstn.t4html.parser.BlockParser;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by stephen on 18/03/2016.
 * Expose block in restapi service
 */
@Path("/blocks")
public class BlocksService {

    @GET()
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Block> findAvailableBlocks() throws IOException {
        List<Block> blockResult = BlockParser
                .read()
                .fileEndsWith(Config.BLOCK_EXTENSION)
                .from("../t4html-core"+ File.separator+"src"+ File.separator+"test"+File.separator+"resources"+File.separator+ "fullSite/html" +File.separator+"blocks")
                .parse();
        return blockResult;
    }

    @GET()
    @Path("/original")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Block> findOriginalBlocks() throws IOException {
        List<Block> blockResult = BlockParser
                .read()
                .fileEndsWith(".html")
                .from("../t4html-core"+ File.separator+"src"+ File.separator+"test"+File.separator+"resources"+File.separator+ "fullSite/html")
                .parse();
        return blockResult;
    }
}

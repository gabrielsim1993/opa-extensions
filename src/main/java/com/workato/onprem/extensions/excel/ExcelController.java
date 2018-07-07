/*
 * Copyright (c) 2018 Workato, Inc. All rights reserved.
 */
package com.workato.onprem.extensions.excel;

import java.util.Map;

//import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ExcelController {
	private final static Logger LOG = LoggerFactory.getLogger(ExcelController.class);

	private final static int MIN_SHEET_INDEX = 0;
	private final static int MIN_OFFSET = 0;
	private final static int MIN_BATCH_SIZE = 1;
	private final static int MAX_BATCH_SIZE = 1000;
	
//	@Inject
//    private Environment env;

    @RequestMapping(path = "/parseExcelByPath", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> parseExcelByPath(@RequestBody Map<String, Object> body) throws Exception {
    	try {
    		LOG.info(body.toString());
    		
        	Assert.notNull(body.get("filepath"), "Missing filepath");
        	Assert.notNull(body.get("sheetIndex"), "Missing sheet index");
        	Assert.notNull(body.get("offset"), "Missing offset");
        	Assert.notNull(body.get("batchSize"), "Missing batch size");
        	
            String filepath = (String) body.get("filepath");
            int sheetIndex = Integer.parseInt((String) body.get("sheetIndex"));
            int offset = Integer.parseInt((String) body.get("offset"));
            int batchSize = Integer.parseInt((String) body.get("batchSize"));

            if (batchSize < MIN_BATCH_SIZE) {
    			throw new Exception("Batch size must be at least " + MIN_BATCH_SIZE);
    		}
            
            if (batchSize > MAX_BATCH_SIZE) {
    			throw new Exception("Exceeded maximum batch size of " + MAX_BATCH_SIZE);
    		}
            
            if (sheetIndex < MIN_SHEET_INDEX) {
    			throw new Exception("Sheet index must be at least " + MIN_SHEET_INDEX);
    		} 
            
            if (offset < MIN_OFFSET) {
    			throw new Exception("Offset must be at least " + MIN_OFFSET);
    		}
    		
            String jsonResponse = ExcelParser.parseExcelByPath(filepath, sheetIndex, offset, batchSize);

    		return new ResponseEntity<String>(jsonResponse, HttpStatus.OK);
            
    	} 
    	catch (Exception e) {
    		e.printStackTrace();
    		return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
    	}
    }
    
    @RequestMapping(path = "/parseExcelMetadataByPath", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> parseExcelMetadataByPath(@RequestBody Map<String, Object> body) {
    	try {
    		LOG.info(body.toString());
    		
        	Assert.notNull(body.get("filepath"), "Missing filepath");
        	
            String filepath = (String) body.get("filepath");
            
            String jsonResponse = ExcelParser.parseExcelMetadataByPath(filepath);

    		return new ResponseEntity<String>(jsonResponse, HttpStatus.OK);
            
    	} 
    	catch (Exception e) {
    		e.printStackTrace();
    		return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
    	}
    }
    
    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> ping() {
    	return new ResponseEntity<>(HttpStatus.OK);
    }
}

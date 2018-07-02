package com.workato.onprem.extensions.excel;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ExcelExtension {
	final static Logger logger = LoggerFactory.getLogger(ExcelExtension.class);
	
	@Inject
    private Environment env;

    @RequestMapping(path = "/parseExcelByPath", method = RequestMethod.POST)
    public @ResponseBody String parseExcelByPath(@RequestBody Map<String, Object> body) throws Exception {
    	try {
    		logger.debug(body.toString());
        	
            String filepath = (String) body.get("filepath");
            int sheetIndex = Integer.parseInt((String) body.get("sheetIndex"));
            int offset = Integer.parseInt((String) body.get("offset"));
            int batchSize = Integer.parseInt((String) body.get("batchSize"));
            
            String jsonReponse = ExcelParser.parseExcelByPath(filepath, sheetIndex, offset, batchSize);
    		return jsonReponse;

    	} 
    	catch (Exception e) {
    		logger.error(e.getStackTrace().toString());

    		e.printStackTrace();
    	}
		return "Error";
    }
}

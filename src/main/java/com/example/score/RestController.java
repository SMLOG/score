package com.example.score;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	   @Autowired
	    private JdbcTemplate jdbcTemplate;

	   @RequestMapping(value = "/test", method = RequestMethod.GET)
	   public String test(@Autowired HttpServletRequest req) {
		 req.getHeaderNames();
		   return "ok";
	   }
	   
	   @RequestMapping(value = "/excel", method = RequestMethod.GET)
	   public String excel(@Autowired HttpServletResponse response,
			   @RequestParam(name="code",required=true) String code,
			   @RequestParam(name="name",required=false) String name
			   ) throws EncryptedDocumentException, InvalidFormatException, IOException {
		   
		   String fileName =(name!=null &&name.trim().length()>0?name.trim():"")+code+".xlsx";
		   fileName=new String(fileName.getBytes("UTF-8"), "ISO8859-1");
		   
	        response.setHeader("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	        response.setHeader("Content-Disposition", "inline; filename=" + fileName);
	        OutputStream outputStream = null;
            outputStream = response.getOutputStream();

	        ExcelUtil.generateExcel(code, jdbcTemplate,outputStream);

		   return null;
	   }
	   

}

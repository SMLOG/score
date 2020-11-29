package com.example.score;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@org.springframework.web.bind.annotation.RestController
public class RestController {
	
	   @Autowired
	    private JdbcTemplate jdbcTemplate;

	   @RequestMapping(value = "/test", method = RequestMethod.GET)
	   public String test(@Autowired HttpServletRequest req) {
		 req.getHeaderNames();
		   return "ok";
	   }

}

package com.opcTjyx.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/variant")
public class VariantController {

	@RequestMapping(value="/goTest")
	public String goTest(HttpServletRequest request) {
		
		//http://localhost:8080/OPCTjyx/variant/goTest
		
		return "test";
	}
}

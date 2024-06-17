package com.masil.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/test")
@Tag(name = "Test", description = "ControllerTest")
public class TestController {
	@Operation(summary = "Test Endpoint", description = "This endpoint is for testing purposes.")
	@GetMapping("/swaggertest")
	public String test(){
		log.info("Testing the endpoint");
		return "test";
	}
}

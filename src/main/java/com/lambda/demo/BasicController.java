package com.lambda.demo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class BasicController {

	@Autowired
	public BasicService service;
	
	@PostMapping("/postit")
	public Mono<Map<String, Object>> postRequest(){
		return service.callLambdaApi();
	}
	
}

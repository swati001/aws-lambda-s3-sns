package com.lambda.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class BasicService {

	private final WebClient webClient;

    public BasicService(WebClient.Builder builder, 
                            @Value("${apigateway.url}") String apiGatewayUrl) {
        this.webClient = builder.baseUrl(apiGatewayUrl).build();
    }
    public Mono<Map<String, Object>> callLambdaApi() {
    	
    	Map<String, Object> requestBody = new HashMap();
        requestBody.put("key", "order1");
        requestBody.put("message", "Hello from API Gateway");

        return webClient.post()
                .uri("")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}

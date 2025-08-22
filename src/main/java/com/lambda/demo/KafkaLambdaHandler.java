package com.lambda.demo;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.RequestHandler;

public class KafkaLambdaHandler implements RequestHandler<Map<String,Object>, Map<String,Object>> {

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input,
			com.amazonaws.services.lambda.runtime.Context context) {
		 Map<String, Object> response = new HashMap<>();
		 context.getLogger().log("Received api gateway Event: " + input.toString());
	        // statusCode is required
	        response.put("statusCode", 200);

	        // headers are optional
	        Map<String, String> headers = new HashMap<>();
	        headers.put("Content-Type", "application/json");
	        response.put("headers", headers);

	        // body must be a string
	        response.put("body", "{\"message\": \"Hello from Lambda via API Gateway! Munia munia munia *******\"}");
	        context.getLogger().log("logging the response sending back to api gateway: " + response.toString());
	        return response;
	}
}

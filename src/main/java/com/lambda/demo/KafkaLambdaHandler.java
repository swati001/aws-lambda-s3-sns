package com.lambda.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.amazonaws.services.lambda.runtime.RequestHandler;

public class KafkaLambdaHandler implements RequestHandler<Map<String,Object>, Map<String,Object>> {

	private static final String BOOTSTRAP_SERVERS = "b-1.msk-cluster.example.com:9092,b-2.msk-cluster.example.com:9092";
    private static final String TOPIC = "your-topic-name";

    // For performance, reuse KafkaProducer across invocations (see AWS Lambda docs)
    private static final KafkaProducer<String, String> producer = createProducer();

    private static KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // Uncomment and configure if your cluster requires SASL/SSL
        // props.put("security.protocol", "SASL_SSL");
        // props.put("sasl.mechanism", "SCRAM-SHA-512");
        // props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"your_username\" password=\"your_password\";");
        // props.put("ssl.truststore.location", "/tmp/kafka.client.truststore.jks"); // Make sure truststore is available

        return new KafkaProducer<>(props);
    }
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input,
			com.amazonaws.services.lambda.runtime.Context context) {
		
		String message = input != null ? input.toString() : "Default message";

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, message);
        Map<String, Object> response = new HashMap<>();
        context.getLogger().log("Received api gateway Event: " + input.toString());
        // statusCode is required
        response.put("statusCode", 200);

        // headers are optional
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.put("headers", headers);

        try {
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get(); // Wait for send to finish
            context.getLogger().log("Message produced to topic: " + metadata.topic() + ", partition: " + metadata.partition() + ", offset: " + metadata.offset());
            response.put("body", "{\"message\": \"Hello from Lambda via API Gateway! message producer successfully*******\"}");
            
        } catch (Exception e) {
            context.getLogger().log("Error producing message: " + e.getMessage());
            response.put("body", "{\"message\": \"Hello from Lambda via API Gateway! message producer failed*******\"}");
        }
	       
	        return response;
	}
}

package com.lambda.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

import net.coobird.thumbnailator.Thumbnails;

public class S3EventHandler implements Function<S3EventNotification, String> {
	
	private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();
    
   // private final String topicArn = "arn:aws:sns:ap-south-1:123456789012:MySNSTopic";

	/*
	 * @Override
	 
	public String handleRequest(S3Event input, Context context) {
		
		context.getLogger().log("Received S3 Event: " + input.toString());

        String bucketName = input.getRecords().get(0).getS3().getBucket().getName();
        String objectKey  = input.getRecords().get(0).getS3().getObject().getKey();

        context.getLogger().log("Bucket: " + bucketName);
        context.getLogger().log("File uploaded: " + objectKey);

        return "Processed file " + objectKey + " from bucket " + bucketName;
	}
*/
	@Override
	public String apply(S3EventNotification t) {
		t.getRecords().forEach(input ->  {
			
		String bucketName = input.getS3().getBucket().getName();
		String key = input.getS3().getObject().getKey();
		if (key.startsWith("thumbnails/")) {
            return;
        }
		//sns.publish(topicArn,"File uploaded to  "+bucketName+" key: "+key);
		if(key.endsWith(".png") || key.endsWith(".jpg"))  {
			 try (InputStream inputStream = s3.getObject(bucketName, key).getObjectContent()) {
				 File tempFile = File.createTempFile("image", ".jpg");
                 try (FileOutputStream out = new FileOutputStream(tempFile)) {
                     Thumbnails.of(inputStream)
                             .size(200, 200)   
                             .toOutputStream(out);
                 }
                
                 String thumbKey = "thumbnails/" + key;
                 s3.putObject(bucketName, thumbKey, tempFile);
			 } catch (AmazonServiceException e) {
				
				e.printStackTrace();
			} catch (SdkClientException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		});
		return "ok";
	}

}

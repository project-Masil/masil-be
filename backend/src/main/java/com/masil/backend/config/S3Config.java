 package com.masil.backend.config;

 import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

 @Configuration
 public class S3Config {

	 @Value("${aws.s3.bucket-name}")
	 private String bucketName;

 	@Bean
 	public S3Client s3Client() {
 		return S3Client.builder()
 			.region(Region.AP_NORTHEAST_2)
 			.credentialsProvider(ProfileCredentialsProvider.create())
 			.build();
 	}

 	@Bean
    public String bucketName() {
        return bucketName;
    }
 }
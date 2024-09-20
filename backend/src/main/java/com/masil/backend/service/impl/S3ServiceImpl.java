 package com.masil.backend.service.impl;

 import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.masil.backend.service.S3Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

 @Service
 public class S3ServiceImpl implements S3Service {

 	@Autowired
 	private S3Client s3Client;

 	@Value("${aws.s3.bucket-name}")
 	private String bucketName;

 	@Override
 	public String uploadFile(MultipartFile file) throws IOException {
 		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

 		try {
 			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
 				.bucket(bucketName)
 				.key(fileName)
 				.contentType(file.getContentType())
 				.build();

 			s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

 			return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
 		} catch (S3Exception e) {
 			throw new RuntimeException("Failed to upload file to S3", e);
 		}
 	}

 	@Override
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        return files.stream().map(file -> {
            try {
                return uploadFile(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload one of the files", e);
            }
        }).collect(Collectors.toList());
    }

 	@Override
    public void deleteFile(String fileName) throws IOException {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

 }
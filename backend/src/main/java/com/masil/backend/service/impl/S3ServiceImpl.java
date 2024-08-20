// package com.masil.backend.service.impl;
//
// import com.masil.backend.service.S3Service;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
// import software.amazon.awssdk.services.s3.S3Client;
// import software.amazon.awssdk.services.s3.model.PutObjectRequest;
// import software.amazon.awssdk.services.s3.model.S3Exception;
//
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.UUID;
//
// @Service
// public class S3ServiceImpl implements S3Service {
//
// 	@Autowired
// 	private S3Client s3Client;
//
// 	@Value("${aws.s3.bucket-name}")
// 	private String bucketName;
//
// 	@Override
// 	public String uploadFile(MultipartFile file) throws IOException {
// 		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
// 		Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
// 		Files.write(tempFile, file.getBytes());
//
// 		try {
// 			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
// 				.bucket(bucketName)
// 				.key(fileName)
// 				.build();
//
// 			s3Client.putObject(putObjectRequest, tempFile);
//
// 			return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();
// 		} catch (S3Exception e) {
// 			throw new RuntimeException("Failed to upload file to S3", e);
// 		} finally {
// 			Files.deleteIfExists(tempFile);
// 		}
// 	}
// }
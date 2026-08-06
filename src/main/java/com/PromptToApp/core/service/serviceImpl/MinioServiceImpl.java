package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.service.minioService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Service
@Slf4j
@RequiredArgsConstructor
public class MinioServiceImpl implements minioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;


    public void uploadFile(String objectPath, InputStream stream, String contentType) throws Exception {
        log.info("this is the bucket name {}", bucketName);
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectPath)
                        .stream(stream, stream.available(), -1)
                        .contentType(contentType)
                        .build()
        );
    }


    public String getFile(String objectPath) throws Exception {
        InputStream fileStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectPath)
                        .build()
        );


        return new String(fileStream.readAllBytes(), StandardCharsets.UTF_8);
    }


    public Iterable<Result<Item>> getAllFileStartsWith(String objectStartsWith) throws Exception {
        Iterable<Result<Item>> objects = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(objectStartsWith)
                        .recursive(true)
                        .build()
        );


        return objects;
    }


    public void copyObject(String sourcePath, String destinationPath) throws Exception {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucketName)
                        .object(destinationPath)
                        .source(
                                CopySource.builder()
                                        .bucket(bucketName) // source bucket
                                        .object(sourcePath)
                                        .build()
                        )
                        .build()
        );

    }


    public void deleteFile(String objectPath) throws Exception {

        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectPath)
                        .build()
        );

    }
}

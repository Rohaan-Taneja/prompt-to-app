package com.PromptToApp.core.service;

import io.minio.Result;
import io.minio.messages.Item;

import java.io.InputStream;

public interface minioService {

    void uploadFile(String objectPath , InputStream stream , String contentType) throws Exception ;

    String getFile(String objectPath) throws Exception;


    Iterable<Result<Item>> getAllFileStartsWith(String objectStartsWith) throws Exception;

    void copyObject(String sourcePath, String destinationPath) throws Exception;

    void deleteFile(String objectPath) throws Exception;
}

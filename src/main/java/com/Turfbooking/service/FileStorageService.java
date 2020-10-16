package com.Turfbooking.service;

import com.Turfbooking.models.response.CommonResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    public String storeFile(MultipartFile fileName);

    public Resource loadFileAsResource(String fileName);

    CommonResponse getFiles();

    CommonResponse deleteFile(String fileName);
}

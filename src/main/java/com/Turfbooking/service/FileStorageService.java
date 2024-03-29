package com.Turfbooking.service;

import com.Turfbooking.models.response.CommonResponse;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileStorageService {

    String storeFile(MultipartFile fileName);

    Resource loadFileAsResource(String fileName);

    CommonResponse getFiles();

    CommonResponse deleteFile(String fileName);
}

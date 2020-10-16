package com.Turfbooking.service.Impl;

import com.Turfbooking.exception.GeneralException;
import com.Turfbooking.models.response.CommonResponse;
import com.Turfbooking.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String path;

    public Path getuploadDir(String path) {
        Path dirPath = Paths.get(path)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(dirPath);
        } catch (Exception ex) {
            throw new GeneralException("Could not create the directory where the uploaded files will be stored.", HttpStatus.OK);
        }
        return dirPath;
    }

    @Override
    public String storeFile(MultipartFile file) throws GeneralException {

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new GeneralException("Sorry! Filename contains invalid path sequence " + fileName, HttpStatus.OK);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = getuploadDir(path).resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new GeneralException("Could not store file " + fileName + ". Please try again!", HttpStatus.OK);
        }
    }

    @Override
    public CommonResponse deleteFile(String nameOfFile) throws GeneralException{


        // Normalize file name
        String fileName = StringUtils.cleanPath(nameOfFile);

        // Check if the file's name contains invalid characters
        if (fileName.contains("..")) {
            throw new GeneralException("Sorry! Filename contains invalid path sequence " + fileName, HttpStatus.OK);
        }

        // Copy file to the target location (Replacing existing file with the same name)
        Path targetLocation = getuploadDir(path).resolve(fileName);

        File file = new File(targetLocation.toString());

        String responseMessage = null;
        if (file.delete())
            responseMessage = "Delete Successfully";
        else
            responseMessage = "Delete Failed";

        return new CommonResponse(null, HttpStatus.OK.value(), responseMessage, true);
    }

    @Override
    public CommonResponse getFiles() {

        Path targetLocation = getuploadDir(path);
        String targetPath = targetLocation.toString() + "/";
        File folder = new File(targetPath);
        String[] files = folder.list();
        CommonResponse commonResponse = new CommonResponse(files, HttpStatus.OK.value(), "Files Fetched Successfully", Boolean.TRUE);
        return commonResponse;

    }

    @Override
    public Resource loadFileAsResource(String fileName) throws GeneralException {
        try {
            Path filePath = getuploadDir(path).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new GeneralException("File not found " + fileName, HttpStatus.OK);
            }
        } catch (MalformedURLException ex) {
            throw new GeneralException("File not found " + fileName, HttpStatus.OK);
        }
    }
}

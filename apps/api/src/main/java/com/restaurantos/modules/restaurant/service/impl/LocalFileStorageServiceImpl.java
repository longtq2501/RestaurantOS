package com.restaurantos.modules.restaurant.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.restaurantos.modules.restaurant.service.FileStorageService;
import com.restaurantos.shared.exception.InvalidInputException;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

/**
 * Local implementation of {@link FileStorageService}.
 * Saves files to the local filesystem.
 */
@Service
@Slf4j
public class LocalFileStorageServiceImpl implements FileStorageService {

    private final Path storageLocation;
    private final String baseUrl;

    public LocalFileStorageServiceImpl(
            @Value("${storage.local.base-path:uploads}") String basePath,
            @Value("${storage.local.base-url}") String baseUrl) {
        this.storageLocation = Paths.get(basePath).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;

        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            log.error("Could not create storage directory", e);
            throw new RuntimeException("Could not create storage directory", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        return uploadInternal(file, folder)[0];
    }

    @Override
    public String[] uploadFileWithThumbnail(MultipartFile file, String folder, int width, int height) {
        return uploadInternalWithThumbnail(file, folder, width, height);
    }

    private String[] uploadInternal(MultipartFile file, String folder) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (originalFileName.contains("..")) {
                throw new InvalidInputException("Invalid file name: " + originalFileName);
            }

            String extension = getExtension(originalFileName);
            String fileName = UUID.randomUUID().toString() + extension;
            Path targetFolder = this.storageLocation.resolve(folder);
            Files.createDirectories(targetFolder);

            Path targetLocation = targetFolder.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return new String[] { baseUrl + "/" + folder + "/" + fileName };
        } catch (IOException e) {
            log.error("Could not store file {}", originalFileName, e);
            throw new RuntimeException("Could not store file " + originalFileName, e);
        }
    }

    private String[] uploadInternalWithThumbnail(MultipartFile file, String folder, int width, int height) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (originalFileName.contains("..")) {
                throw new InvalidInputException("Invalid file name: " + originalFileName);
            }

            String extension = getExtension(originalFileName);
            String baseName = UUID.randomUUID().toString();
            String fileName = baseName + extension;
            String thumbName = baseName + "_thumb" + extension;

            Path targetFolder = this.storageLocation.resolve(folder);
            Files.createDirectories(targetFolder);

            // Save original
            Path targetLocation = targetFolder.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Save thumbnail
            Path thumbLocation = targetFolder.resolve(thumbName);
            Thumbnails.of(targetLocation.toFile())
                    .size(width, height)
                    .toFile(thumbLocation.toFile());

            return new String[] {
                    baseUrl + "/" + folder + "/" + fileName,
                    baseUrl + "/" + folder + "/" + thumbName
            };
        } catch (IOException e) {
            log.error("Could not store file {}", originalFileName, e);
            throw new RuntimeException("Could not store file " + originalFileName, e);
        }
    }

    private String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return i > 0 ? fileName.substring(i) : "";
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(baseUrl)) {
            return;
        }

        String relativePath = fileUrl.substring(baseUrl.length());
        Path filePath = this.storageLocation
                .resolve(relativePath.startsWith("/") ? relativePath.substring(1) : relativePath);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Could not delete file at {}", filePath, e);
        }
    }
}

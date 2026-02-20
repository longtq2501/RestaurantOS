package com.restaurantos.modules.restaurant.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for file storage operations.
 */
public interface FileStorageService {

    /**
     * Uploads a file to the storage.
     *
     * @param file   the file to upload
     * @param folder the target folder in the storage
     * @return the public URL or identifier of the uploaded file
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * Uploads a file and generates a thumbnail.
     *
     * @param file   the file to upload
     * @param folder the target folder
     * @param width  thumbnail width
     * @param height thumbnail height
     * @return an array containing [fileUrl, thumbnailUrl]
     */
    String[] uploadFileWithThumbnail(MultipartFile file, String folder, int width, int height);

    /**
     * Deletes a file from the storage.
     *
     * @param fileUrl the public URL or identifier of the file to delete
     */
    void deleteFile(String fileUrl);
}

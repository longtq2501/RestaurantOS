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
     * Deletes a file from the storage.
     *
     * @param fileUrl the public URL or identifier of the file to delete
     */
    void deleteFile(String fileUrl);
}

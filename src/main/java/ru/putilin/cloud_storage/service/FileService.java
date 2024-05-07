package ru.putilin.cloud_storage.service;

import org.springframework.web.multipart.MultipartFile;
import ru.putilin.cloud_storage.entity.File;

import java.util.List;

public interface FileService {

    void uploadFile(String fileName, MultipartFile file);

    File downloadFile(String fileName);

    void delete(String fileName);

    void editNameOfFile(String fileName, String newFileName);

    List<File> listOfFiles(int limit);
}

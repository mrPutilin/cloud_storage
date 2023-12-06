package ru.putilin.cloud_storage.filemanager;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.putilin.cloud_storage.entity.File;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
public class FileManager {

    private final static String STORE = "src/main/resources/storage";

    public File createFile(String fileName, MultipartFile source) {
        LocalDateTime uploadedTimeOfFile = LocalDateTime.now();
        String hash = new DigestUtils("SHA3-256").digestAsHex(source.getOriginalFilename() + uploadedTimeOfFile);
        File uploadedFile = new File();
        uploadedFile.setFileName(fileName);
        uploadedFile.setFileSize(source.getSize());
        uploadedFile.setUploadDate(uploadedTimeOfFile);
        uploadedFile.setHash(hash);
        uploadedFile.setType(source.getContentType());

        return uploadedFile;
    }

    public void upload(String fileName, MultipartFile resource) throws IOException {
        Path path = Paths.get(STORE, fileName);
        Path file = Files.createFile(path);

        try (FileOutputStream out = new FileOutputStream(file.toString());
             BufferedOutputStream bout = new BufferedOutputStream(out)) {
            bout.write(resource.getBytes(), 0, resource.getBytes().length);
        }

    }

    public void delete(String fileName) throws IOException {
        Path path = Paths.get(STORE, fileName);
        Files.delete(path);
    }

    public Resource download(String fileName) throws MalformedURLException {
        Path path = Paths.get(STORE, fileName);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new MalformedURLException();
        }
    }

    public void renameFile(String fileName, String newName) throws IOException {
        Path source = Paths.get(STORE, fileName);
        Files.move(source, source.resolveSibling(newName));
    }

}

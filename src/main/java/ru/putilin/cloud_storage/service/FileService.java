package ru.putilin.cloud_storage.service;

import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.putilin.cloud_storage.dao.FileDAO;
import ru.putilin.cloud_storage.dto.EditFilenameDTO;
import ru.putilin.cloud_storage.dto.FileDTO;
import ru.putilin.cloud_storage.dto.TokenDTO;
import ru.putilin.cloud_storage.dto.UserDTO;
import ru.putilin.cloud_storage.entity.File;
import ru.putilin.cloud_storage.entity.JWTToken;
import ru.putilin.cloud_storage.entity.User;
import ru.putilin.cloud_storage.filemanager.FileManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FileService {

    private final FileDAO fileDAO;
    private final FileManager fileManager;
    private final ModelMapper modelMapper;

    public FileService(FileDAO fileDAO, FileManager fileManager, ModelMapper modelMapper) {
        this.fileDAO = fileDAO;
        this.fileManager = fileManager;
        this.modelMapper = modelMapper;
    }

    public void uploadFile(String fileName ,MultipartFile file) throws IOException {
        File uploadedNewFile = fileManager.createFile(fileName, file);
        fileManager.upload(fileName, file);
        fileDAO.save(uploadedNewFile);
    }


    public FileDTO downloadFile(String fileName) throws MalformedURLException {
        Optional<File> file = fileDAO.findByFileName(fileName);
        FileDTO fileDTO = new FileDTO();
        Resource downloadingFile = fileManager.download(fileName);
        if (file.isPresent()) {
            fileDTO.setHash(file.get().getHash());
            fileDTO.setFile(downloadingFile.toString());
        }

        return fileDTO;
    }

    public void delete(String fileName) throws IOException {
        fileManager.delete(fileName);
        fileDAO.deleteFileByFileName(fileName);
    }

    public void editNameOfFile(String fileName, EditFilenameDTO newFileName) {
        Optional<File> file = fileDAO.findByFileName(fileName);
        if (file.isPresent()) {
            file.get().setFileName(newFileName.getName());
            fileDAO.save(file.get());
            fileManager.renameFile(fileName, newFileName.getName());
        }

    }

    public Map<String, Long> listOfFiles(int limit) {
        return fileDAO.findAll(Pageable.ofSize(limit))
                .stream()
                .collect(Collectors.toMap(File::getFileName, File::getFileSize));
    }




    public TokenDTO convert(JWTToken token) {
        return this.modelMapper.map(token, TokenDTO.class);
    }

    public User convertUserDtoToUser(UserDTO userDTO) {
        return this.modelMapper.map(userDTO, User.class );
    }



}

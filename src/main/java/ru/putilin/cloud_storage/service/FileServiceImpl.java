package ru.putilin.cloud_storage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.putilin.cloud_storage.dao.FileDAO;
import ru.putilin.cloud_storage.entity.File;
import ru.putilin.cloud_storage.exception.ExceptionRelatedHandleFile;
import ru.putilin.cloud_storage.exception.IncorrectInputException;
import ru.putilin.cloud_storage.filemanager.FileManager;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class FileServiceImpl implements FileService {

    private final static Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);
    private final FileDAO fileDAO;
    private final FileManager fileManager;

    public FileServiceImpl(FileDAO fileDAO, FileManager fileManager) {
        this.fileDAO = fileDAO;
        this.fileManager = fileManager;
    }

    public void uploadFile(String fileName, MultipartFile file) throws ExceptionRelatedHandleFile {
        if (file == null) {
            LOG.info("File not attached");
            throw new IncorrectInputException("File not attached");
        }
        if (fileDAO.findByFileName(fileName).isPresent()) {
            LOG.info("File already exist in the database");
            throw new IncorrectInputException("File already exist");
        }

       try {
           File uploadedNewFile = fileManager.createFile(fileName, file);
           fileDAO.save(uploadedNewFile);
           LOG.info("File {} uploaded in the database", fileName);
           fileManager.upload(fileName, file);
           LOG.info("File {} uploaded in the store", fileName);
       } catch (IOException e) {
           LOG.warn("File {} exist already in the store", fileName);
           throw new ExceptionRelatedHandleFile("File exist already");

       }

    }

    public File downloadFile(String fileName) throws ExceptionRelatedHandleFile {

        Optional<File> file = fileDAO.findByFileName(fileName);
        if (file.isEmpty()) {
            LOG.warn("File {} is not exist in the database", fileName);
            throw new IncorrectInputException("File is not exist");
        }

        try {
        Resource downloadingFile = fileManager.download(fileName);
        file.get().setFile(downloadingFile.toString());
        LOG.info("File {} downloaded from the store", fileName);
        } catch (IOException e) {
            LOG.warn("File {} has not been found in the store", fileName);
            throw new ExceptionRelatedHandleFile("File has not been found");
        }

        return file.get();
    }

    public void delete(String fileName) throws ExceptionRelatedHandleFile {
        try {
            if (fileDAO.findByFileName(fileName).isEmpty()) {
                LOG.warn("File {} doesn't exist in database", fileName);
                throw new IncorrectInputException("File doesn't exist");
            }
            fileDAO.deleteFileByFileName(fileName);
            fileManager.delete(fileName);
        } catch (IOException e) {
            LOG.warn("File {} has not bee found in the store", fileName);
            throw new ExceptionRelatedHandleFile("File has not been found");
        }

    }

    public void editNameOfFile(String fileName, String newFileName) throws ExceptionRelatedHandleFile {
        Optional<File> file = fileDAO.findByFileName(fileName);
        if (file.isPresent()) {
            file.get().setFileName(newFileName);
            fileDAO.save(file.get());
            LOG.info("Name of the file {} edited", fileName);
        } else {
            LOG.warn("File {} is not exist in the database", fileName);
            throw new IncorrectInputException("File is not exist");
        }

        try {
            fileManager.renameFile(fileName, newFileName);
            LOG.info("File {} renamed in the store", fileName);
        } catch (IOException e) {
            LOG.warn("File {} isn't exist in the store", fileName);
            throw new ExceptionRelatedHandleFile("File isn't exist");
        }

    }

    public List<File> listOfFiles(int limit) {
        return fileDAO.findAll(Pageable.ofSize(limit))
                .stream()
                .collect(Collectors.toList());
    }



}

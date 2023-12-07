package ru.putilin.cloud_storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import ru.putilin.cloud_storage.dao.FileDAO;
import ru.putilin.cloud_storage.entity.File;
import ru.putilin.cloud_storage.exception.IncorrectInputException;
import ru.putilin.cloud_storage.filemanager.FileManager;
import ru.putilin.cloud_storage.service.FileService;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    private String fileName;
    String fileHash;
    Long fileSize;
    File mockedFileEntity;

    MockMultipartFile mockMultipartFile;

    @Mock
    FileDAO fileDAO;
    @Mock
    FileManager fileManager;

    @InjectMocks
    FileService fileService;

    @BeforeEach
    void setup() {
        this.mockedFileEntity = new File("Test", 456666L, "text/plain", "8765", LocalDateTime.now());
        this.fileName = "Test";
        this.fileHash = "8765";
        this.fileSize = 456666L;
        this.mockMultipartFile = new MockMultipartFile("TestFile", new byte[]{1, 4, 5});

    }

    @Test
    void uploadFileWithFileAttachedSaveFileTest() {
        Mockito.when(fileManager.createFile(fileName, mockMultipartFile)).thenReturn(mockedFileEntity);
        Assertions.assertEquals(mockedFileEntity, fileManager.createFile(fileName, mockMultipartFile));

    }

    @Test
    void uploadfileWhenFileNotAttachedThrowException() {
        Exception a = Assertions.assertThrowsExactly(IncorrectInputException.class,
                () -> fileService.uploadFile(fileName, null));

        Assertions.assertEquals("File not attached", a.getMessage());

    }

    @Test
    void uploadFileWhenFileExistAlready() {
        when(fileDAO.findByFileName(fileName)).thenReturn(Optional.of(mockedFileEntity));
        Assertions.assertTrue(fileDAO.findByFileName(fileName).isPresent());
    }

    @Test
    void uploadFileThenFileDaoSaveFileVerifyOneTime() {
        when(fileDAO.findByFileName(fileName)).thenReturn(Optional.empty());
        Mockito.when(fileManager.createFile(fileName, mockMultipartFile)).thenReturn(mockedFileEntity);
        fileService.uploadFile(fileName, mockMultipartFile);
        verify(fileDAO, times(1)).save(mockedFileEntity);
    }

    @Test
    void uploadFileWhenFileExistThrowException() {
        when(fileDAO.findByFileName(fileName)).thenReturn(Optional.of(mockedFileEntity));
        Exception a = Assertions.assertThrowsExactly(IncorrectInputException.class,
                () -> fileService.uploadFile(fileName, mockMultipartFile));
        Assertions.assertEquals("File already exist", a.getMessage());

    }

    @Test
    void downloadFileWhenFileExistReturnOk() throws MalformedURLException {
        doReturn(Optional.of(mockedFileEntity)).when(fileDAO).findByFileName(fileName);
        Path path = Paths.get("uriForTest");
        Resource resource = new UrlResource(path.toUri());
        mockedFileEntity.setFile(resource.toString());
        when(fileManager.download(fileName)).thenReturn(resource);

        Assertions.assertEquals(mockedFileEntity, fileService.downloadFile(fileName));

    }

    @Test
    void deleteWhenFileNotExistThenThrowException() {
        when(fileDAO.findByFileName(fileName)).thenReturn(Optional.empty());
        Exception ex = Assertions.assertThrowsExactly(IncorrectInputException.class, () -> fileService.delete(fileName));
        Assertions.assertEquals(ex.getMessage(), "File doesn't exist");
    }

    @Test
    void deleteWhenFileExistVerifyFileDaoDelete() throws IOException {
        when(fileDAO.findByFileName(fileName)).thenReturn(Optional.ofNullable(mockedFileEntity));
        fileService.delete(fileName);
        verify(fileManager, times(1)).delete(fileName);
        verify(fileDAO, times(1)).deleteFileByFileName(fileName);
    }

    @Test
    void editFilenameThenReturnOk() throws IOException {
        when(fileDAO.findByFileName(fileName)).thenReturn(Optional.of(mockedFileEntity));
        doNothing().when(fileManager).renameFile(fileName, "newFileName");
        fileService.editNameOfFile(fileName, "newFileName");
        verify(fileDAO, times(1)).save(mockedFileEntity);
    }

    @Test
    void listOfFilesThenReturnOk() {
        List<File> testList = new ArrayList<>();
        testList.add(mockedFileEntity);
        testList.add(mockedFileEntity);
        testList.add(mockedFileEntity);

        Page<File> testObjectPage = new PageImpl<>(testList);

        when(fileDAO.findAll(Pageable.ofSize(3))).thenReturn(testObjectPage);

        Assertions.assertEquals(fileService.listOfFiles(3).size(), testList.size());

    }

}

package ru.putilin.cloud_storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;
import ru.putilin.cloud_storage.dto.EditFilenameDTO;
import ru.putilin.cloud_storage.entity.File;
import ru.putilin.cloud_storage.filemanager.FileManager;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "spring.liquibase.contexts=test")
public class FileServiceIT {

    private int port;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FileManager fileManager;

    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));

    @Container
    private final static GenericContainer<?> fileService = new GenericContainer<>("cloudimage").withExposedPorts(8080);

    @BeforeEach
    void upset() {
        port = fileService.getMappedPort(8080);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void testUploadFile_withRightFile_returnOK () throws Exception {
        String url = "http://localhost:" + port + "/cloud/file";

        MockMultipartFile file = new MockMultipartFile("file" ,
                "file1.txt",
                MediaType.MULTIPART_FORM_DATA.getType(),
                "text for test".getBytes());

        File createdFile = new File();
        createdFile.setFileName("file1");
        createdFile.setFileSize(232L);

        Mockito.when(fileManager.createFile("file1", file)).thenReturn(createdFile);


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart(url)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content(file.getBytes())
                .param("filename", "file1");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("ROLE_USER")
    void testUploadFile_withNoAttachedFile_ReturnErrorMessage() throws Exception {
        String url = "http://localhost:" + port + "/cloud/file";
        MockMultipartFile file = new MockMultipartFile("notFile", new byte[0]);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart(url)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("filename", "file");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.message").value("File not attached"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("uploadMethod /api/file?filename=file return Exception message File already exist")
    void test3() throws Exception {
        String url = "http://localhost:" + port + "/cloud/file";
        MockMultipartFile file = new MockMultipartFile("file", "text".getBytes());
        MockHttpServletRequestBuilder request = multipart(url)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("filename", "file");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("File already exist"),
                        jsonPath("$.id").isNumber());
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/file?filename=file with Object with new fileName, return ОК")
    void test2() throws Exception {
        String url = "http://localhost:" + port + "/cloud/file";

        EditFilenameDTO updateFileName = new EditFilenameDTO();
        updateFileName.setFilename("file3");

        MockHttpServletRequestBuilder request = put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateFileName))
                .param("filename", "file");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    void testDeleteFile_ReturnOk() throws Exception {

        String url = "http://localhost:" + port + "/cloud/file";

        MockHttpServletRequestBuilder request1 = delete(url)
                .param("filename", "file");

        this.mockMvc.perform(request1)
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "USER")
    void testListOfFiles_ReturnForbidden() throws Exception {
        String url = "http://localhost:" + port + "/cloud/list";

        MockHttpServletRequestBuilder request = get(url)
                .param("limit", "2");

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void downloadFileReturnOk() throws Exception {
        String url = "http://localhost:" + port + "/cloud/file";
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(url)
                .param("filename", "testfile");

        Path path = Paths.get(URI.create("uriForTest").toString());
        Resource resource = new UrlResource(path.toUri());
        Mockito.when(fileManager.download("testfile")).thenReturn(resource);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hash").exists())
                .andExpect(jsonPath("$.file").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void downloadFileUserHasNoAuthoritiesReturnExceptionForbidden() throws Exception {
        String url = "http://localhost:" + port + "/cloud/file";
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(url)
                .param("filename", "testfile");

        Path path = Paths.get(URI.create("uriForTest").toString());
        Resource resource = new UrlResource(path.toUri());
        Mockito.when(fileManager.download("testfile")).thenReturn(resource);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    // метод для преобразования объекта в json, применяется прям в запросе
    public static String asJsonString(Object body) {
        try {
            return new ObjectMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}


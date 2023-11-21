package ru.putilin.cloud_storage.controller;

import jakarta.annotation.security.RolesAllowed;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.putilin.cloud_storage.dao.TokenDAO;
import ru.putilin.cloud_storage.dto.EditFilenameDTO;
import ru.putilin.cloud_storage.dto.FileDTO;
import ru.putilin.cloud_storage.dto.UserDTO;
import ru.putilin.cloud_storage.securityconfiguration.JWTUtil;
import ru.putilin.cloud_storage.service.FileService;
import ru.putilin.cloud_storage.service.MyService;
import ru.putilin.cloud_storage.service.UserServiceImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

@RestController
public class CloudController {

    private final MyService myService;
    private final FileService fileService;


    public CloudController(MyService myService, FileService fileService) {
        this.myService = myService;
        this.fileService = fileService;

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        return myService.login(userDTO);
    }

    @RolesAllowed("USER")
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("ALL OK!");
    }


    @PostMapping(value = "/file")
    @RolesAllowed("USER")
    public void uploadFile(@RequestParam("filename") String fileName, @RequestBody MultipartFile file) throws IOException {
      fileService.uploadFile(fileName, file);
    }

    @DeleteMapping(value = "/file")
    @RolesAllowed("USER")
    public void deleteFile(@RequestParam("filename") String fileName) throws IOException {
        fileService.delete(fileName);
    }

    @RolesAllowed(value = "USER")
    @GetMapping(value = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> download(@RequestParam("filename") String fileName) throws MalformedURLException {
        return ResponseEntity.ok().body(fileService.downloadFile(fileName)) ;
    }

    @RolesAllowed(value = "USER")
    @PutMapping("/file")
    public void editNameOfFile(@RequestParam("filename") String fileName, @RequestBody EditFilenameDTO newFileName) {
    fileService.editNameOfFile(fileName, newFileName);
    }

    @RolesAllowed("USER")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("dd");
    }


    @RolesAllowed("USER")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Long>> listOfFiles (@RequestParam("limit") int limit) {
        return ResponseEntity.ok(fileService.listOfFiles(limit));
    }


}

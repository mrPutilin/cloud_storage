package ru.putilin.cloud_storage.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.putilin.cloud_storage.dto.*;
import ru.putilin.cloud_storage.entity.File;
import ru.putilin.cloud_storage.exception.ExceptionRelatedHandleFile;
import ru.putilin.cloud_storage.service.FileService;
import ru.putilin.cloud_storage.service.LoginService;
import ru.putilin.cloud_storage.service.LogoutService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cloud")
public class CloudController {

    private final LoginService loginService;
    private final LogoutService logoutService;
    private final FileService fileService;
    private final ModelMapper modelMapper;


    public CloudController(LoginService loginService, LogoutService logoutService, FileService fileService, ModelMapper modelMapper) {
        this.loginService = loginService;
        this.logoutService = logoutService;
        this.fileService = fileService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenDTO login(@RequestBody UserDTO userDTO) {
        return loginService.login(userDTO);
    }

    @PostMapping(value = "/file")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void uploadFile(@RequestParam("filename") String fileName, @RequestBody MultipartFile file) {
        fileService.uploadFile(fileName, file);
    }

    @DeleteMapping(value = "/file")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@RequestParam("filename") String fileName) {
        fileService.delete(fileName);
    }

    @RolesAllowed(value = "USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/file")
    public FileDTO downloadFile(@RequestParam("filename") String fileName) throws ExceptionRelatedHandleFile {
        return convertFileToFileDTO(fileService.downloadFile(fileName));
    }

    @RolesAllowed(value = "ADMIN")
    @PutMapping("/file")
    @ResponseStatus(HttpStatus.OK)
    public void editNameOfFile(@RequestParam("filename") String fileName, @RequestBody EditFilenameDTO newFileName) throws ExceptionRelatedHandleFile {
        fileService.editNameOfFile(fileName, newFileName.getFilename());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<FileListDTO> listOfFiles(@RequestParam("limit") int limit) {

        return fileService.listOfFiles(limit)
                .stream()
                .map(this::convertFileToFileListDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutService.logout(request, response, authentication);
    }

    public FileDTO convertFileToFileDTO(File file) {
        return this.modelMapper.map(file, FileDTO.class);
    }

    public FileListDTO convertFileToFileListDTO(File file) {return this.modelMapper.map(file, FileListDTO.class);
    }

}

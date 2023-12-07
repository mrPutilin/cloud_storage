package ru.putilin.cloud_storage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "files")
public class File {

    public File() {
    }

    public File(String fileName, Long fileSize, String type, String hash, LocalDateTime uploadDate) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.type = type;
        this.hash = hash;
        this.uploadDate = uploadDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", unique = true)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "type")
    private String type;

    @Column(name = "hash")
    private String hash;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Transient
    private String file;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file1 = (File) o;
        return Objects.equals(id, file1.id) && Objects.equals(fileName, file1.fileName) && Objects.equals(fileSize, file1.fileSize) && Objects.equals(type, file1.type) && Objects.equals(hash, file1.hash) && Objects.equals(uploadDate, file1.uploadDate) && Objects.equals(file, file1.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, fileSize, type, hash, uploadDate, file);
    }
}

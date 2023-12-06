package ru.putilin.cloud_storage.dto;

public class FileListDTO {
    private String filename;
    private Long size;

    public FileListDTO() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}

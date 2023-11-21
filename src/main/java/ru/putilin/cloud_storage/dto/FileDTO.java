package ru.putilin.cloud_storage.dto;

public class FileDTO {

    private String hash;
    private String file;

    public FileDTO() {
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}

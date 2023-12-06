package ru.putilin.cloud_storage.dto;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "FileDTO{" +
                "hash='" + hash + '\'' +
                ", file='" + file + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDTO fileDTO = (FileDTO) o;
        return Objects.equals(hash, fileDTO.hash) && Objects.equals(file, fileDTO.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, file);
    }
}

package ru.putilin.cloud_storage.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.putilin.cloud_storage.entity.File;

import java.util.Optional;

@Repository
public interface FileDAO extends JpaRepository<File, Long> {

    void deleteFileByFileName(String fileName);

    Optional<File> findByFileName(String fileName);
}

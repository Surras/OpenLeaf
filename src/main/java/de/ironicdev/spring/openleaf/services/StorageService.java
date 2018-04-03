package de.ironicdev.spring.openleaf.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void storeJPEG(MultipartFile file);

    void storeJPEG(MultipartFile file, String filenName);

    void storeFile(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    void delete(String fileName);
}

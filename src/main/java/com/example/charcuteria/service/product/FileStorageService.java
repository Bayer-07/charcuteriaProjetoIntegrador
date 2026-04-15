package com.example.charcuteria.service.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    // Não sei muito sobre isso, essas 2 foram as docs q eu usei se precisarem
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html#isEmpty()
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/file/Files.html

    // path do diretorio que vai ser salva as imagens
    private final String uploadDir = "src/main/resources/static/uploads/products/";

    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {return null;}

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException error) {
            // fazer um erro melhor pra aparecer no front
            System.err.println("Error: " + error.getMessage());
            throw new RuntimeException("Cannot save file", error);
        }
    }

    public void deleteFile(String fileName) throws IOException{
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        Files.deleteIfExists(filePath);
    }
}

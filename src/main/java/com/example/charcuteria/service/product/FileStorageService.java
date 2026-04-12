package com.example.charcuteria.service.product;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

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
        } catch (Exception e) {
            // fazer um erro melhor pra aparecer no front
            System.out.println(e);
            throw new RuntimeException("Cannot save");
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            // logar o erro, nao posso dar throw pq é void (nao sei oq faço com isso KKKKKKKKK)
            System.out.println(e);
        }
    }
}

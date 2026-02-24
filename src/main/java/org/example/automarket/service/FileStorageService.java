package org.example.automarket.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads/cars");  // loyiha ildizida "uploads/cars" papkasi

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Uploads papkasi yaratib bo‘lmadi", e);
        }
    }

    /**
     * Rasmni serverga saqlaydi va URL qaytaradi
     * @param file yuklanayotgan rasm
     * @param carAdId e'lon ID si (papka nomi uchun)
     * @return saqlangan rasmning URL si (masalan /uploads/cars/123/image.jpg)
     */
    public String store(MultipartFile file, Long carAdId) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Fayl bo'sh");
            }

            // Fayl nomini unique qilish (UUID + original name)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID() + extension;

            // Papka: uploads/cars/{carAdId}
            Path carDir = rootLocation.resolve(carAdId.toString());
            Files.createDirectories(carDir);

            Path destination = carDir.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // URL qaytarish (frontend uchun /uploads/... ishlatiladi)
            return "/uploads/cars/" + carAdId + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Rasm saqlanmadi", e);
        }
    }

    // Agar kerak bo'lsa: rasmni o'chirish
    public void delete(String fileUrl) {
        try {
            // URL dan path ni ajratib olish
            Path filePath = Paths.get("." + fileUrl); // loyiha ildizidan
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // O'chirishda xato bo'lsa log qilish mumkin, lekin to'xtatmaymiz
            System.err.println("Rasm o'chirishda xato: " + e.getMessage());
        }
    }
}

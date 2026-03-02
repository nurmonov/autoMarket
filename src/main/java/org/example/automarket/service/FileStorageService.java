package org.example.automarket.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
@Slf4j
public class FileStorageService {

    private static final String UPLOAD_ROOT = "uploads";

    // Papka nomi uchun enum (keyinchalik qo‘shish oson bo‘lishi uchun)
    public enum EntityType {
        CARS("cars"),
        BRANDS("brands"),
        MODELS("models");

        private final String folderName;

        EntityType(String folderName) {
            this.folderName = folderName;
        }

        public String getFolderName() {
            return folderName;
        }
    }

    public String store(MultipartFile file, Long entityId, EntityType entityType) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Fayl bo'sh yoki null");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";

            // Fayl nomini unique qilish (UUID + extension)
            String filename = UUID.randomUUID() + extension;

            // Papka: uploads/{entityType}/{entityId}
            String subFolder = entityType.getFolderName() + "/" + entityId;
            Path dir = Paths.get(UPLOAD_ROOT, subFolder);
            Files.createDirectories(dir);

            Path destination = dir.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Qaytariladigan URL
            String url = "/" + UPLOAD_ROOT + "/" + subFolder + "/" + filename;
            log.info("Fayl saqlandi: {}", url);

            return url;

        } catch (IOException e) {
            log.error("Fayl saqlashda xato: {}", e.getMessage(), e);
            throw new RuntimeException("Rasm saqlanmadi", e);
        }
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        try {
            // URL dan real path ni olish
            Path path = Paths.get("." + fileUrl);
            if (Files.deleteIfExists(path)) {
                log.info("Fayl o'chirildi: {}", fileUrl);
            }
        } catch (IOException e) {
            log.warn("Fayl o'chirishda xato: {}", e.getMessage());
        }
    }
}
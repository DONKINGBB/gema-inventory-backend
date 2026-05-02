package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final String UPLOAD_DIR = "uploads/";

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam(value = "folder", defaultValue = "general") String folder,
            @RequestParam("file") MultipartFile file) {
        
        Map<String, String> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("error", "Error: Archivo vacío");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // Determinar la subcarpeta (perfil, producto o general)
            String subFolder = "general/";
            if ("profiles".equalsIgnoreCase(folder)) {
                subFolder = "profiles/";
            } else if ("products".equalsIgnoreCase(folder)) {
                subFolder = "products/";
            }

            // Crear el directorio completo si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR + subFolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre de archivo único
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            int i = originalFileName.lastIndexOf('.');
            if (i > 0) {
                extension = originalFileName.substring(i);
            }
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // Guardar el archivo en la subcarpeta
            Path targetLocation = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Devolver la URL con la subcarpeta incluida
            String fileUrl = "/uploads/" + subFolder + uniqueFileName;
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);

        } catch (IOException ex) {
            ex.printStackTrace();
            response.put("error", "Error al guardar el archivo en el servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

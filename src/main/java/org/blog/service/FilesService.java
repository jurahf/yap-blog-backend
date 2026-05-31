package org.blog.service;

import org.blog.model.PostImage;
import org.blog.repository.PostImageRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class FilesService {
    public static final String UPLOAD_DIR = "uploads/";
    public final PostImageRepository repository;

    public FilesService(PostImageRepository repository) {
        this.repository = repository;
    }

    public PostImage upload(long postId, MultipartFile file) {
        // найдем текущую картинку, если есть, и удалим файл
        Optional<PostImage> opt = repository.getByPostId(postId);

        if (opt.isPresent()) {
            String oldPath = opt.get().getPath();
            deleteFileFromDisk(oldPath);
        }

        try {
            // Сохраняем новую картинку на диск
            String newFilePath = saveFileToDisk(postId, file);

            // Сохраняем ссылку в БД
            repository.createOrUpdate(postId, newFilePath);

            return repository.getByPostId(postId).get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Resource download(long postId) {
        try {
            Optional<PostImage> opt = repository.getByPostId(postId);
            String filename = opt.map(PostImage::getPath).orElseThrow();

            Path filePath = Paths.get(filename);
            byte[] content = Files.readAllBytes(filePath);

            return new ByteArrayResource(content);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String saveFileToDisk(long postId, MultipartFile file) throws IOException {
        // Создаем директорию, если она не существует
        Path uploadDir = Paths.get(UPLOAD_DIR).resolve(Long.toString(postId));
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Сохраняем файл
        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        file.transferTo(filePath);

        return filePath.toString();
    }

    private void deleteFileFromDisk(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Логируем ошибку, но не прерываем выполнение
            System.err.println("Failed to delete old image: " + filePath + ", error: " + e.getMessage());
        }
    }
}

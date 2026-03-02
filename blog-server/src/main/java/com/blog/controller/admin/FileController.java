package com.blog.controller.admin;

import com.blog.common.Result;
import com.blog.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class FileController {

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioConfig minioConfig;

    @PostConstruct
    public void init() {
        try {
            String bucketName = minioConfig.getBucketName();
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化MinIO Bucket失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String objectName = buildObjectPath(extension);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            String url = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + objectName;
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    private String buildObjectPath(String extension) {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());
        String fileName = UUID.randomUUID().toString() + extension;
        return year + "/" + month + "/" + day + "/" + fileName;
    }
}

package com.blog.controller.admin;

import com.blog.annotation.RequireAuth;
import com.blog.common.Result;
import com.blog.config.MinioConfig;
import io.minio.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
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
    @RequireAuth
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
            String url = "/api/admin/file/" + objectName;
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/file/{year}/{month}/{day}/{filename:.+}")
    @RequireAuth
    public void getFile(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String filename,
            javax.servlet.http.HttpServletResponse response) {
        String objectName = year + "/" + month + "/" + day + "/" + filename;
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(objectName)
                        .build())) {
            String contentType = getContentType(filename);
            response.setContentType(contentType);
            response.setHeader("Cache-Control", "max-age=86400");
            byte[] buffer = new byte[8192];
            OutputStream os = response.getOutputStream();
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        } catch (Exception e) {
            response.setStatus(404);
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

    private String getContentType(String filename) {
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (ext) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            default:
                return "application/octet-stream";
        }
    }
}

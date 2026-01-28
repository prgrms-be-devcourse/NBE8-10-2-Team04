package com.back.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3ImageService {

    private final S3Client s3Client; // S3Template 대신 S3Client 사용

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName = UUID.randomUUID() + "-" + originalFileName;

        try {
            // 1. 업로드 요청 객체 생성
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storeFileName)
                    .contentType(multipartFile.getContentType()) // 컨텐츠 타입 설정
                    .build();

            // 2. 실제 업로드 실행
            s3Client.putObject(putOb, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 IO 에러 발생", e);
        }

        // 3. URL 생성 (S3Client는 URL을 String으로 바로 안 줘서 직접 만들거나 getUrl 사용)
        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder().bucket(bucketName).key(storeFileName).build())
                .toString();
    }
}
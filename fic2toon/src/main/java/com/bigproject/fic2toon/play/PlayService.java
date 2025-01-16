package com.bigproject.fic2toon.play;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service

public class PlayService {
    private final String apiUrl = "http://127.0.0.1:8000/text_to_webtoon"; // FastAPI 엔드포인트 URL

    public String sendTextToApi(MultipartFile file) {
        try {
            // Resource로 변환
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // MultiValueMap 생성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("text", resource);

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // HttpEntity 생성
            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            // RestTemplate으로 요청 전송
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiUrl, requestEntity, String.class
            );

            return response.getBody();
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 중 오류 발생", e);
        }
    }
}
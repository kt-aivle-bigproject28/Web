package com.bigproject.fic2toon.play;

import com.bigproject.fic2toon.client.api.FastApiClient;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@Slf4j

public class PlayController {
    private final FastApiClient fastApiClient;

    @PostMapping("/text_to_webtoon")
    public String textToWebtoon(@RequestParam("webtoon") MultipartFile text, Model model) throws IOException {
        try {
            String originalFilename = text.getOriginalFilename();

            // 파일 확장자 확인
            if (originalFilename == null || !originalFilename.endsWith(".txt")) {
                model.addAttribute("error", "올바른 파일 형식이 아닙니다. .txt 파일만 업로드해주세요.");
                return "playmodel";
            }

            // MIME 타입 확인
            if (!text.getContentType().equals("text/plain")) {
                model.addAttribute("error", "올바른 파일 형식이 아닙니다. .txt 파일만 업로드해주세요.");
                return "playmodel";
            }




            // FastApiClient를 통해 API 호출
            String resultWebtoon = fastApiClient.textoWebtoon(text);
            model.addAttribute("resultWebtoon", resultWebtoon); // 결과를 모델에 추가
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "파일 처리 실패: " + e.getMessage()); // 오류 메시지를 모델에 추가
        }
        return "playmodel"; // play.html로 이동
    }

    @GetMapping("/playmodel")
    public String goplaymodel(){
        return "model/playmodel";
    }
}

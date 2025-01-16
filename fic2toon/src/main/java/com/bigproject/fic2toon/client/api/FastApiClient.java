package com.bigproject.fic2toon.client.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "fastApiClient", url = "${webtoon.api.host}")
public interface FastApiClient {

//    @GetMapping("/hospital_by_module")
//    public List<HospitalResponse> getHospital(@RequestParam("request") String request, @RequestParam("latitude") double latitude, @RequestParam("longitude") double longitude, @RequestParam("address") String address, @RequestParam("number_of_hospitals") int number_of_hospitals);

    @PostMapping(value = "/text_to_webtoon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String textoWebtoon(@RequestPart("webtoon") MultipartFile webtoon);
}
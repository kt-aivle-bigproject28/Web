package com.bigproject.fic2toon.board;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
    private Long id;
    private String title;
    private String content;
    private Integer boardType;
    private String image;
    private LocalDateTime createdTime;
    private Long userId;
}
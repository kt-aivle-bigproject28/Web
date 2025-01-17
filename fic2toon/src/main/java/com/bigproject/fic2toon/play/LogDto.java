package com.bigproject.fic2toon.play;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogDto {
    private String title;
    private String path;
    private String userUid;
    private String createdTime;
}

package com.bigproject.fic2toon.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserDto {


    private String id;


    private String pw;


    private String name;
    private String phone;

    public UserDto(User user) {
        this.id = user.getId();
        this.pw = user.getPw();
        this.name = user.getName();
        this.phone = user.getPhone();
    }
}
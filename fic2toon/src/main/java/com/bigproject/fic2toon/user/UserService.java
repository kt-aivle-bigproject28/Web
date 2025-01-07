package com.bigproject.fic2toon.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(UserDto userDto) {
        if (userRepository.existsById(userDto.getId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .id(userDto.getId())
                .pw(passwordEncoder.encode(userDto.getPw()))
                .name(userDto.getName())
                .phone(userDto.getPhone())
                .idType(0) // 기본 사용자 유형 설정
                .build();

        return userRepository.save(user);
    }

    public User login(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(userDto.getPw(), user.getPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public String findPassword(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!user.getName().equals(userDto.getName())) {
            throw new IllegalArgumentException("이름이 일치하지 않습니다.");
        }

        if (!user.getPhone().equals(userDto.getPhone())) {
            throw new IllegalArgumentException("전화번호가 일치하지 않습니다.");
        }

        // 주의: 실제 비밀번호를 반환하는 것은 보안상 위험할 수 있습니다.
        // 대신 임시 비밀번호를 생성하여 반환하거나, 비밀번호 재설정 링크를 제공하는 것이 좋습니다.
        return user.getPw();
    }
}
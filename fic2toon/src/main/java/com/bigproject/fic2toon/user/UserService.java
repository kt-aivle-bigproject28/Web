package com.bigproject.fic2toon.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ID 중복 확인 메서드
    public boolean isIdExists(String id) {
        return userRepository.findById(id).isPresent();
    }

    // 단순 사용자 생성 메서드
    public boolean createUser(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // 사용자 생성 메서드 (회원가입)
    @Transactional
    public User create(UserDto userDto) {


        System.out.println("회원가입 요청: " + userDto);


        if (isIdExists(userDto.getId())) { // ID 중복 확인

            System.out.println("중복된 ID: " + userDto.getId());


            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .id(userDto.getId())
                .pw(passwordEncoder.encode(userDto.getPw()))
                .name(userDto.getName())
                .phone(userDto.getPhone())
                .idType(0) // 기본 사용자 유형 설정
                .build();



        System.out.println("저장할 사용자: " + user);

        return userRepository.save(user);
    }

    // 로그인 처리
    public User login(UserDto userDto) {
        System.out.println("로그인 요청 ID: " + userDto.getId());

        // 데이터베이스에서 사용자 검색
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        System.out.println("데이터베이스에서 검색된 사용자: " + user);

        // 비밀번호 검증
        if (!passwordEncoder.matches(userDto.getPw(), user.getPw())) {
            System.out.println("비밀번호 불일치");
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        System.out.println("로그인 성공: " + user.getId());
        return user;
    }


    // 비밀번호 찾기
    public String findPassword(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!user.getName().equals(userDto.getName())) {
            throw new IllegalArgumentException("이름이 일치하지 않습니다.");
        }

        if (!user.getPhone().equals(userDto.getPhone())) {
            throw new IllegalArgumentException("전화번호가 일치하지 않습니다.");
        }

        // 주의: 실제 비밀번호 반환은 보안상 위험합니다.
        return user.getPw();
    }
}

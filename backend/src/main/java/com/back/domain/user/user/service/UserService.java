package com.back.domain.user.user.service;

import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.repository.UserRepository;
import com.back.global.exception.ServiceException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthTokenService authTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public long count() {
        return userRepository.count();
    }

    public User join(String loginId, String password, String email) {
        userRepository
                .findByLoginId(loginId)
                .ifPresent(_user -> {
                    throw new ServiceException("409-1", "이미 존재하는 아이디입니다.");
                });
        password = passwordEncoder.encode(password); //패스워드 암호화 추가

        User user = new User(loginId, password, email);
        return userRepository.save(user);
    }

    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }


    public void deleteById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 회원입니다."));
        userRepository.deleteById(id);
    }

    public void checkPassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");
    }

    public Optional<User> findByApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey);
    }

    public String genAccessToken(User user) {
        return authTokenService.genAccessToken(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 프로필(이메일) 수정
     * PATCH /api/v1/user/me
     * 이메일 변경 시 기존 토큰 무효화 (tokenVersion 증가 + apiKey 재생성)
     */
    @Transactional
    public User updateProfile(long id, @NotBlank @Size(min = 2, max = 30) String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 회원입니다."));

        user.modifyUser(email, user.getPassword());

        // 기존 토큰 무효화: tokenVersion 증가 + apiKey 재생성
        user.increaseTokenVersion();
        user.modifyApiKey(UUID.randomUUID().toString());

        return user;
    }

    /**
     * 비밀번호 변경
     * PATCH /api/v1/user/me/password
     * 현재 비밀번호 검증 후 새 비밀번호로 변경
     * 비밀번호 변경 시 기존 모든 토큰 무효화 (tokenVersion 증가 + apiKey 재생성)
     */
    @Transactional
    public User changePassword(
            long id,
            @NotBlank @Size(min = 2, max = 30) String currentPassword,
            @NotBlank @Size(min = 2, max = 20) String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ServiceException("403-1", "현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ServiceException("400-1", "새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.modifyUser(user.getEmail(), encodedPassword);

        // 기존 모든 토큰 무효화: tokenVersion 증가 + apiKey 재생성
        user.increaseTokenVersion();
        user.modifyApiKey(UUID.randomUUID().toString());

        return user;
    }
}

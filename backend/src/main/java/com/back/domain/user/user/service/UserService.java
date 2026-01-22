package com.back.domain.user.user.service;

import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.repository.UserRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

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

    public Map<String, Object> payload(String accessToken) {
        return authTokenService.payload(accessToken);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}

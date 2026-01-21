package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.global.exception.ServiceException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final AuthTokenService authTokenService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public long count() {
        return memberRepository.count();
    }

    public Member join(String loginId, String password, String email) {
        memberRepository
                .findByLoginId(loginId)
                .ifPresent(_member -> {
                    throw new ServiceException("409-1", "이미 존재하는 아이디입니다.");
                });
        password = passwordEncoder.encode(password); //패스워드 암호화 추가

        Member member = new Member(loginId, password, email);
        return memberRepository.save(member);
    }

    public Optional<Member> findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }


    public void deleteById(Long id) {
        memberRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 회원입니다."));
        memberRepository.deleteById(id);
    }

    public void checkPassword(Member member, String password) {
        if (!passwordEncoder.matches(password, member.getPassword()))
            throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }

    public String genAccessToken(Member member) {
        return authTokenService.genAccessToken(member);
    }

    public Map<String, Object> payload(String accessToken) {
        return authTokenService.payload(accessToken);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Transactional
    public Member updateMember(
            long id,
            @NotBlank @Size(min = 2, max = 30) String email,
            @NotBlank @Size(min = 2, max = 20) String password)
    {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 회원입니다."));

        String encodedPassword = passwordEncoder.encode(password);
        member.modifyMember(email, encodedPassword);

        return memberRepository.save(member);
    }
}
